"""
polygon.py — Polygon.io ETF 데이터 서비스
"""
import requests
from config import POLYGON_API_KEY


def get_etf_list(db_conn) -> list:
    """DB에서 ETF 목록 전체 조회"""
    cursor = db_conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT symbol, price, annual_div, div_yield, div_count, updated_at
        FROM etf_info
        ORDER BY div_yield DESC
    """)
    rows = cursor.fetchall()
    cursor.close()
    return rows


def get_etf_detail(ticker: str, db_conn) -> dict:
    """DB에서 ETF 상세 조회"""
    cursor = db_conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT symbol, price, annual_div, div_yield, div_count, updated_at
        FROM etf_info
        WHERE symbol = %s
    """, (ticker.upper(),))
    row = cursor.fetchone()
    cursor.close()
    return row


def get_etf_dividends(ticker: str) -> list:
    """Polygon.io에서 ETF 배당 내역 조회"""
    r = requests.get(
        "https://api.polygon.io/v3/reference/dividends",
        params={
            "ticker": ticker.upper(),
            "limit": 12,
            "order": "desc",
            "apiKey": POLYGON_API_KEY,
        },
        timeout=10,
    )
    data = r.json()
    return data.get("results", [])