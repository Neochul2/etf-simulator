"""
load_etf_master.py — CSV에서 시총 상위 100개 추출 후 Polygon으로 재수집 → MySQL 적재
실행: python scripts/load_etf_master.py
"""
import requests
import pandas as pd
import time
import sys
import os
from datetime import datetime, timedelta

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from config import POLYGON_API_KEY
from database import get_connection

CSV_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "etfdb_screener_6.29.csv")
TOP_N = 100


def parse_assets(val):
    try:
        return float(str(val).replace("$", "").replace(",", ""))
    except:
        return 0


def get_price(ticker):
    r = requests.get(
        f"https://api.polygon.io/v2/aggs/ticker/{ticker}/prev",
        params={"apiKey": POLYGON_API_KEY},
        timeout=10,
    )
    results = r.json().get("results", [])
    return results[0]["c"] if results else None


def get_dividends_12m(ticker):
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
        timeout=10,
    )
    return r.json().get("results", [])


# 1. CSV 로드 + 시총 상위 100개 추출
print("CSV 로드 중...")
df = pd.read_csv(CSV_PATH, header=1)
df.columns = df.columns.str.strip()
df["assets_num"] = df["Assets"].apply(parse_assets)
top100 = df.sort_values("assets_num", ascending=False).head(TOP_N)
tickers = top100["Symbol"].tolist()
print(f"시총 상위 {len(tickers)}개 추출 완료")
print(f"상위 5개: {tickers[:5]}")

# 2. DB 연결 + 기존 데이터 삭제
conn = get_connection()
cursor = conn.cursor()
cursor.execute("DELETE FROM etf_info")
conn.commit()
print("기존 etf_info 데이터 삭제 완료")

# 3. Polygon 수집 + 적재
print(f"\nPolygon 수집 시작 (약 43분 소요)\n")
print(f"{'NO':<4} {'티커':<8} {'가격':>8} {'연배당':>10} {'배당률':>8} {'횟수':>5}")
print("-" * 50)

success = 0
failed = []

sql = """
INSERT INTO etf_info
    (symbol, price, annual_div, div_yield, div_count, updated_at)
VALUES
    (%s, %s, %s, %s, %s, %s)
ON DUPLICATE KEY UPDATE
    price      = VALUES(price),
    annual_div = VALUES(annual_div),
    div_yield  = VALUES(div_yield),
    div_count  = VALUES(div_count),
    updated_at = VALUES(updated_at)
"""

for i, ticker in enumerate(tickers, 1):
    print(f"[{i:>3}/{TOP_N}] {ticker:<8}", end=" ", flush=True)
    try:
        price = get_price(ticker)
        time.sleep(13)
        divs = get_dividends_12m(ticker)
        time.sleep(13)

        if price is None:
            print("가격 없음")
            failed.append(ticker)
            continue

        annual_div = sum(d["cash_amount"] for d in divs)
        div_yield = round(annual_div / price * 100, 2) if price else 0

        cursor.execute(sql, (
            ticker,
            round(price, 2),
            round(annual_div, 4),
            div_yield,
            len(divs),
            datetime.now().strftime("%Y-%m-%d"),
        ))
        conn.commit()
        success += 1
        print(f"${price:>7.2f} | ${annual_div:>8.4f} | {div_yield:>6.2f}% | {len(divs)}회")

    except Exception as e:
        print(f"오류: {e}")
        failed.append(ticker)

cursor.close()
conn.close()

print(f"\n완료 — 성공: {success}개 / 실패: {len(failed)}개")
if failed:
    print(f"실패 종목: {failed}")