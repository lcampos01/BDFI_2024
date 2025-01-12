FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
COPY ./web .

ENV PROJECT_HOME=/app
ENV FLASK_ENV=development


RUN pip install --no-cache-dir -r requirements.txt






CMD ["python3", "predict_flask.py"]
