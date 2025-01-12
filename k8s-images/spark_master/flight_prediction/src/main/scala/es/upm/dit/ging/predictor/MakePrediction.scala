package es.upm.dit.ging.predictor

import com.mongodb.spark._
//import com.mongodb.spark.MongoSpark
//import com.mongodb.spark.config.WriteConfig
import org.apache.spark.ml.classification.RandomForestClassificationModel
import org.apache.spark.ml.feature.{Bucketizer, StringIndexerModel, VectorAssembler}
import org.apache.spark.sql.functions.{concat, from_json, lit, to_json, struct}
import org.apache.spark.sql.types.{DataTypes, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.expr

object MakePrediction {

  def main(args: Array[String]): Unit = {
    println("Fligth predictor starting...")

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    //Load the arrival delay bucketizer
    val base_path= "/opt/bitnami/spark/"
    val arrivalBucketizerPath = s"$base_path/models/arrival_bucketizer_2.0.bin"
    println(s"Usando arrivalBucketizer en ruta: $arrivalBucketizerPath")
    val arrivalBucketizer = Bucketizer.load(arrivalBucketizerPath)

    val columns= Seq("Carrier","Origin","Dest","Route")

    //Load all the string field vectorizer pipelines into a dict
    val stringIndexerModelPaths = columns.map { col =>
      s"$base_path/models/string_indexer_model_$col.bin"
    }
    val stringIndexerModels = stringIndexerModelPaths.map(StringIndexerModel.load)
    val stringIndexerMap   = (columns zip stringIndexerModels).toMap

    // Load the numeric vector assembler
    val vectorAssemblerPath = s"$base_path/models/numeric_vector_assembler.bin"
    val vectorAssembler = VectorAssembler.load(vectorAssemblerPath)

    // Load the classifier model
    val randomForestModelPath = s"$base_path/models/spark_random_forest_classifier.flight_delays.5.0.bin"
    val rfc = RandomForestClassificationModel.load(randomForestModelPath)

    // ============== LECTURA DESDE KAFKA ==============
    // Asegúrate de reemplazar "mi_topic_entrada" por el tópico desde el que realmente quieras leer
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "kafka:9092")
      .option("subscribe", "flight_delay_classification_request")  // <--- Ajusta aquí tu tópico
      .load()

    df.printSchema()

    val flightJsonDf = df.selectExpr("CAST(value AS STRING)")

    val struct = new StructType()
      .add("Origin",         DataTypes.StringType)
      .add("FlightNum",      DataTypes.StringType)
      .add("DayOfWeek",      DataTypes.IntegerType)
      .add("DayOfYear",      DataTypes.IntegerType)
      .add("DayOfMonth",     DataTypes.IntegerType)
      .add("Dest",           DataTypes.StringType)
      .add("DepDelay",       DataTypes.DoubleType)
      .add("Prediction",     DataTypes.StringType)
      .add("Timestamp",      DataTypes.TimestampType)
      .add("FlightDate",     DataTypes.DateType)
      .add("Carrier",        DataTypes.StringType)
      .add("UUID",           DataTypes.StringType)
      .add("Distance",       DataTypes.DoubleType)
      .add("Carrier_index",  DataTypes.DoubleType)
      .add("Origin_index",   DataTypes.DoubleType)
      .add("Dest_index",     DataTypes.DoubleType)
      .add("Route_index",    DataTypes.DoubleType)

    val flightNestedDf = flightJsonDf.select(from_json($"value", struct).as("flight"))
    flightNestedDf.printSchema()

    // DataFrame para la parte de vectorización por separado
    val flightFlattenedDf = flightNestedDf.selectExpr(
      "flight.Origin",
      "flight.DayOfWeek",
      "flight.DayOfYear",
      "flight.DayOfMonth",
      "flight.Dest",
      "flight.DepDelay",
      "flight.Timestamp",
      "flight.FlightDate",
      "flight.Carrier",
      "flight.UUID",
      "flight.Distance"
    )
    flightFlattenedDf.printSchema()

    // Creamos "Route"
    val predictionRequestsWithRouteMod = flightFlattenedDf.withColumn(
      "Route",
      concat(flightFlattenedDf("Origin"), lit("-"), flightFlattenedDf("Dest"))
    )

    // Dataframe para vectorizar columnas numéricas
    val flightFlattenedDf2 = flightNestedDf.selectExpr(
      "flight.Origin",
      "flight.DayOfWeek",
      "flight.DayOfYear",
      "flight.DayOfMonth",
      "flight.Dest",
      "flight.DepDelay",
      "flight.Timestamp",
      "flight.FlightDate",
      "flight.Carrier",
      "flight.UUID",
      "flight.Distance",
      "flight.Carrier_index",
      "flight.Origin_index",
      "flight.Dest_index",
      "flight.Route_index"
    )
    flightFlattenedDf2.printSchema()

    val predictionRequestsWithRouteMod2 = flightFlattenedDf2.withColumn(
      "Route",
      concat(flightFlattenedDf2("Origin"), lit("-"), flightFlattenedDf2("Dest"))
    )

    // Aplica los StringIndexerModel que correspondan (si fuera necesario):
    // (En tu código original intentas hacerlo, pero no estás usando los resultados.
    //  Normalmente aplicarías algo como:)
    // val transformedDf = stringIndexerMap("Carrier").transform(predictionRequestsWithRouteMod)
    // Y así para "Origin", "Dest", "Route".
    // Pero dado que ya tienes indices en flightFlattenedDf2, quizá NO sea necesario aquí,
    // o tendrías que concatenarlos con lo que ya traes. Depende de tu pipeline real.

    // Vectorizamos las columnas numéricas
    val vectorizedFeatures = vectorAssembler
      .setHandleInvalid("keep")
      .transform(predictionRequestsWithRouteMod2)

    vectorizedFeatures.printSchema()

    // Eliminamos columnas extra de índices
    val finalVectorizedFeatures = vectorizedFeatures
      .drop("Carrier_index")
      .drop("Origin_index")
      .drop("Dest_index")
      .drop("Route_index")

    finalVectorizedFeatures.printSchema()

    // Hacemos la predicción
    val predictions = rfc.transform(finalVectorizedFeatures)
      .drop("Features_vec")

    // Nos quedamos con un DF final sin columnas de metadatos del modelo
    val finalPredictions = predictions
      .drop("indices")
      .drop("values")
      .drop("rawPrediction")
      .drop("probability")

    finalPredictions.printSchema()

    // ============== ESCRITURA EN MONGODB ==============
    val dataStreamWriter = finalPredictions

      //spark.readStream

      //.schema(finalPredictions.schema)
      //.load()
      // manipulate your streaming data
      .writeStream
      .format("mongodb")
      .option("spark.mongodb.connection.uri", "mongodb://mongo:27017")
      .option("spark.mongodb.database", "agile_data_science")
      .option("checkpointLocation", "/tmp")
      .option("spark.mongodb.collection", "flight_delay_classification_response")
      .outputMode("append")
    
    val kafkaWriter = finalPredictions
      .selectExpr("CAST(UUID AS STRING) AS key", "to_json(struct(*)) AS value")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "kafka:9092")
      .option("topic", "flight_delay_classification_response")
      .option("checkpointLocation", "/tmp/kafka-checkpoints")
      //.start()
    val query = dataStreamWriter.start()
    val query_kafka = kafkaWriter.start()
    // Console Output for predictions

    val consoleOutput = finalPredictions.writeStream
      .outputMode("append")
      .format("console")
      .start()
    consoleOutput.awaitTermination()
  }
}
