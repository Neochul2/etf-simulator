"""
test_koreaexim_connection.py — 한국수출입은행 환율 API 연결 테스트
실행: python tests/test_koreaexim_connection.py
"""
import requests
from datetime import datetime, timedelta

KOREAEXIM_API_KEY = "***REMOVED***"

def get_rate(date_str):
    r = requests.get(
        "https://oapi.koreaexim.go.kr/site/program/financial/exchangeJSON",
        params={
            "authkey": KOREAEXIM_API_KEY,
            "searchdate": date_str,
            "data": "AP01",
        },
        timeout=10,
    )
    data = r.json()
    for item in data:
        if item.get("cur_unit") == "USD":
            return float(item["deal_bas_r"].replace(",", ""))
    return None

print("=" * 45)
print("  한국수출입은행 USD/KRW 환율 연결 테스트")
print("=" * 45)

for i in range(10):
    date_str = (datetime.now() - timedelta(days=i)).strftime("%Y%m%d")
    rate = get_rate(date_str)
    if rate:
        print(f"✅ 한국수출입은행 API 연결 완료")
        print(f"기준일 : {date_str}")
        print(f"환율   : 1 USD = {rate:,.2f} KRW")
        break
else:
    print("❌ 환율 데이터 없음 (API 키 확인 필요)")

print("=" * 45)