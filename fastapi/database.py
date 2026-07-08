"""
database.py — MySQL 연결 관리
"""
import mysql.connector
from mysql.connector import Error
from config import DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME


def get_connection():
    """MySQL 연결 반환"""
    try:
        conn = mysql.connector.connect(
            host=DB_HOST,
            port=DB_PORT,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
        )
        return conn
    except Error as e:
        print(f" DB 연결 실패: {e}")
        raise


def test_connection():
    """DB 연결 테스트"""
    try:
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT VERSION()")
        version = cursor.fetchone()
        print(f"✅ DB 연결 완료 — MySQL {version[0]}")
        cursor.close()
        conn.close()
    except Error as e:
        print(f"❌ DB 연결 실패: {e}")


if __name__ == "__main__":
    test_connection()