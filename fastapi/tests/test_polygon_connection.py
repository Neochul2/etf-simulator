"""
test_polygon_connection.py — Polygon.io API 연결 테스트
실행: python tests/test_polygon_connection.py
"""
import requests

POLYGON_API_KEY = "***REMOVED***" # 수정포인트 직접 키넣음 

r = requests.get(
    "https://api.polygon.io/v2/aggs/ticker/AAPL/prev",
    params={"apiKey": POLYGON_API_KEY},
    timeout=10,
)

print(f"HTTP 상태코드 : {r.status_code}")

if r.status_code == 200:
    data = r.json()
    print("✅ Polygon API 연결 완료")
    print(f"AAPL 전일 종가 : ${data['results'][0]['c']}")
elif r.status_code == 403:
    print("❌ 인증 실패 (API 키 확인 필요)")
else:
    print(f"❌ 오류: {r.text[:200]}")