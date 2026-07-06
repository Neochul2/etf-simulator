"""
step2_load_dividend.py — 적재된 100개 ETF의 배당 내역 12개월치 적재
실행: python scripts/step2_load_dividend.py
"""
import requests
import time
import sys
import os
from datetime import datetime, timedelta

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from config import POLYGON_API_KEY
from database import get_connection


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


# 1. DB에서 100개 티커 목록 가져오기
conn = get_connection()
cursor = conn.cursor()
cursor.execute("SELECT symbol FROM etf_info")
tickers = [row[0] for row in cursor.fetchall()]
print(f"대상 종목: {len(tickers)}개")

# 2. Polygon 수집 + 업서트 (삭제 없음 — 증분 적재)
sql = """
INSERT INTO etf_dividend
    (symbol, ex_div_date, pay_date, cash_amount)
VALUES
    (%s, %s, %s, %s)
ON DUPLICATE KEY UPDATE
    pay_date    = VALUES(pay_date),
    cash_amount = VALUES(cash_amount)
"""

success = 0
failed = []

for i, ticker in enumerate(tickers, 1):
    print(f"[{i:>3}/{len(tickers)}] {ticker:<8}", end=" ", flush=True)
    try:
        divs = get_dividends_12m(ticker)
        time.sleep(13)

        if not divs:
            print("배당 내역 없음")
            continue

        for d in divs:
            cursor.execute(sql, (
                ticker,
                d.get("ex_dividend_date"),
                d.get("pay_date"),
                d.get("cash_amount"),
            ))
        conn.commit()
        success += 1
        print(f"{len(divs)}건 적재 완료")

    except Exception as e:
        print(f"오류: {e}")
        failed.append(ticker)

cursor.close()
conn.close()

print(f"\n완료 — 성공: {success}개 / 실패: {len(failed)}개")
if failed:
    print(f"실패 종목: {failed}")