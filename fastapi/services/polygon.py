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


def get_etf_dividends(ticker: str, db_conn) -> list:
    """DB에서 ETF 배당 내역 조회 (최근 12개월, 최신순)"""
    cursor = db_conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT symbol, ex_div_date, pay_date, cash_amount
        FROM etf_dividend
        WHERE symbol = %s
        ORDER BY ex_div_date DESC
    """, (ticker.upper(),))
    rows = cursor.fetchall()
    cursor.close()
    return rows