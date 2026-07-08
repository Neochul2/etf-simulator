"""
step4_update_prices.py — DB에 있는 100개 티커 기준으로
Polygon에서 최신 가격/배당 재조회 → 업서트 (환율 제외)
실행: python scripts/step4_update_prices.py
"""
import requests
import time
import sys
import os
from datetime import datetime, timedelta

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from config import POLYGON_API_KEY
from database import get_connection
from telegram_notify import notify_success, notify_failure


def get_ohlcv(ticker):
    try:
        r = requests.get(
            f"https://api.polygon.io/v2/aggs/ticker/{ticker}/prev",
            params={"apiKey": POLYGON_API_KEY},
            timeout=5,
        )
        results = r.json().get("results", [])
        if not results:
            return None
        return {
            "close":  results[0]["c"],
            "open":   results[0]["o"],
            "high":   results[0]["h"],
            "low":    results[0]["l"],
            "volume": int(results[0]["v"]),
        }
    except Exception:
        return None


def get_ticker_detail(ticker):
    try:
        r = requests.get(
            f"https://api.polygon.io/v3/reference/tickers/{ticker}",
            params={"apiKey": POLYGON_API_KEY},
            timeout=5,
        )
        result = r.json().get("results", {})
        return result.get("name", "")
    except Exception:
        return ""


def get_dividends_12m(ticker):
    try:
        one_year_ago = (datetime.now() - timedelta(days=365)).strftime("%Y-%m-%d")
        r = requests.get(
            "https://api.polygon.io/v3/reference/dividends",
            params={
                "ticker": ticker,
                "ex_dividend_date.gte": one_year_ago,
                "limit": 50,
                "order": "desc",
                "apiKey": POLYGON_API_KEY,
            },
            timeout=5,
        )
        return r.json().get("results", [])
    except Exception:
        return []


conn = get_connection()
cursor = conn.cursor()

cursor.execute("SELECT symbol FROM etf_info")
tickers = [row[0] for row in cursor.fetchall()]
print(f"갱신 대상: {len(tickers)}개")

sql_info = """
UPDATE etf_info
SET price      = %s,
    open_price = %s,
    high_price = %s,
    low_price  = %s,
    volume     = %s,
    issuer     = %s,
    annual_div = %s,
    div_yield  = %s,
    div_count  = %s,
    updated_at = %s
WHERE symbol = %s
"""

sql_div = """
INSERT INTO etf_dividend (symbol, ex_div_date, pay_date, cash_amount)
VALUES (%s, %s, %s, %s)
ON DUPLICATE KEY UPDATE
    pay_date    = VALUES(pay_date),
    cash_amount = VALUES(cash_amount)
"""

success = 0
failed = []

try:
    for i, ticker in enumerate(tickers, 1):
        print(f"[{i:>3}/{len(tickers)}] {ticker:<8}", end=" ", flush=True)
        try:
            ohlcv  = get_ohlcv(ticker)
            issuer = get_ticker_detail(ticker)
            divs   = get_dividends_12m(ticker)

            time.sleep(12)

            if ohlcv is None:
                print("가격 없음")
                failed.append(ticker)
                continue

            annual_div = sum(d["cash_amount"] for d in divs)
            div_yield  = round(annual_div / ohlcv["close"] * 100, 2) if ohlcv["close"] else 0

            cursor.execute(sql_info, (
                round(ohlcv["close"], 2),
                round(ohlcv["open"],  2),
                round(ohlcv["high"],  2),
                round(ohlcv["low"],   2),
                ohlcv["volume"],
                issuer,
                round(annual_div, 4),
                div_yield,
                len(divs),
                datetime.now().strftime("%Y-%m-%d"),
                ticker,
            ))

            for d in divs:
                cursor.execute(sql_div, (
                    ticker,
                    d.get("ex_dividend_date"),
                    d.get("pay_date"),
                    d.get("cash_amount"),
                ))

            conn.commit()
            success += 1
            print(f"${ohlcv['close']:>7.2f} | {div_yield:>6.2f}% | {issuer[:25]}")

        except Exception as e:
            print(f"오류: {e}")
            failed.append(ticker)

    print(f"\n완료 — 성공: {success}개 / 실패: {len(failed)}개")
    if failed:
        print(f"실패 종목: {failed}")

    notify_success(etf_count=success, failed_count=len(failed), failed_list=failed[:5])

except Exception as e:
    notify_failure(str(e))

finally:
    cursor.close()
    conn.close()