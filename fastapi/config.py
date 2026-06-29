# config.py
import os
from dotenv import load_dotenv

load_dotenv()

# Polygon.io
POLYGON_API_KEY = os.getenv("POLYGON_API_KEY", "***REMOVED***")

# 한국수출입은행
KOREAEXIM_API_KEY = os.getenv("KOREAEXIM_API_KEY", "***REMOVED***")

# MySQL
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", "3307"))
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "mysql")
DB_NAME = os.getenv("DB_NAME", "etf_db")