"""
step3_update_all.py — DB에 있는 100개 티커 기준으로 Polygon에서 최신 가격/배당 재조회 → 업서트
+ 한국수출입은행 환율도 같이 업서트
+ OHLCV(시작가/고가/저가/거래량), ETF 이름(issuer) 추가
CSV 사용 안 함 (반복 실행용)
실행: python scripts/step3_update_all.py
"""
import requests
import time
import sys
import os
from datetime import datetime, timedelta

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from config import POLYGON_API_KEY
from database import get_connection
from services.koreaexim import get_usdkrw


def get_ohlcv(ticker):
    """
    Polygon /v2/aggs/ticker/{ticker}/prev
    전일 종가(c), 시작가(o), 고가(h), 저가(l), 거래량(v) 반환
    데이터 없으면 None 반환
    """
    r = requests.get(
        f"https://api.polygon.io/v2/aggs/ticker/{ticker}/prev",
        params={"apiKey": POLYGON_API_KEY},
        timeout=10,
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


def get_ticker_detail(ticker):
    """
    Polygon /v3/reference/tickers/{ticker}
    ETF 전체 이름(name)을 issuer 컬럼에 저장
    market_cap은 무료 플랜 미제공
    """
    try:
        r = requests.get(
            f"https://api.polygon.io/v3/reference/tickers/{ticker}",
            params={"apiKey": POLYGON_API_KEY},
            timeout=10,
        )
        result = r.json().get("results", {})
        return result.get("name", "")
    except Exception:
        return ""


def get_dividends_12m(ticker):
    """
    Polygon /v3/reference/dividends
    최근 12개월 배당 내역 반환
    """
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


conn = get_connection()
cursor = conn.cursor()

# 0. 환율 먼저 업서트 (한국수출입은행)
print("환율 조회 중...")
try:
    rate_data = get_usdkrw()
    rate_date = f"{rate_data['date'][:4]}-{rate_data['date'][4:6]}-{rate_data['date'][6:8]}"
    cursor.execute("""
        INSERT INTO exchange_rate (base_cur, target_cur, rate, rate_date)
        VALUES (%s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE rate = VALUES(rate)
    """, (rate_data["base"], rate_data["target"], rate_data["rate"], rate_date))
    conn.commit()
    print(f"✅ USD/KRW = {rate_data['rate']} (기준일: {rate_data['date']}) 업서트 완료\n")
except Exception as e:
    print(f"❌ 환율 적재 실패: {e}\n")

# 1. DB에서 기존 티커 목록 가져오기
cursor.execute("SELECT symbol FROM etf_info")
tickers = [row[0] for row in cursor.fetchall()]
print(f"갱신 대상: {len(tickers)}개 (기존 etf_info 기준)")



# 2. 가격/배당/OHLCV/ETF이름 업서트
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
        print(f"${ohlcv['close']:>7.2f} | ${annual_div:>8.4f} | {div_yield:>6.2f}% | {len(divs)}회 | {issuer[:25]}")

    except Exception as e:
        print(f"오류: {e}")
        failed.append(ticker)

cursor.close()
conn.close()

print(f"\n완료 — 성공: {success}개 / 실패: {len(failed)}개")
if failed:
    print(f"실패 종목: {failed}")