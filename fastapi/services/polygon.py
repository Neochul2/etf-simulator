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
    cursor = db_conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT symbol, price, open_price, high_price, low_price,
               volume, issuer, description, annual_div, div_yield, 
               div_count, updated_at
        FROM etf_info WHERE symbol = %s
    """, (ticker.upper(),))
    row = cursor.fetchone()
    cursor.close()
    if not row:
        return None
    # camelCase로 변환 (Java EtfInfoVO 필드명과 일치)
    return {
        "symbol":      row["symbol"],
        "price":       row["price"],
        "openPrice":   row["open_price"],
        "highPrice":   row["high_price"],
        "lowPrice":    row["low_price"],
        "volume":      row["volume"],
        "issuer":      row["issuer"],
        "description": row["description"],
        "annualDiv":   row["annual_div"],
        "divYield":    row["div_yield"],
        "divCount":    row["div_count"],
        "updatedAt":   str(row["updated_at"]) if row["updated_at"] else None,
    }


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