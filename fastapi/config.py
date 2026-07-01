# config.py
import os
from dotenv import load_dotenv

load_dotenv()

POLYGON_API_KEY = os.getenv("POLYGON_API_KEY")
KOREAEXIM_API_KEY = os.getenv("KOREAEXIM_API_KEY")

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", "3307"))
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_NAME = os.getenv("DB_NAME", "etf_db")

if not POLYGON_API_KEY or not KOREAEXIM_API_KEY:
    raise RuntimeError(".env 파일에 API 키가 설정되지 않았습니다.")