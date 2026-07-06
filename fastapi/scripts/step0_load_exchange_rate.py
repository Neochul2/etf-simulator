"""
step0_load_exchange_rate.py — 한국수출입은행 API에서 USD/KRW 환율 조회 → MySQL 증분 업서트
실행: python scripts/step0_load_exchange_rate.py
"""
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from services.koreaexim import get_usdkrw
from database import get_connection

# 1. 한국수출입은행 API 호출
print("환율 조회 중...")
data = get_usdkrw()
print(f"USD/KRW = {data['rate']} (기준일: {data['date']})")

# 2. DB 업서트 (rate_date 기준 unique key)
conn = get_connection()
cursor = conn.cursor()

sql = """
INSERT INTO exchange_rate
    (base_cur, target_cur, rate, rate_date)
VALUES
    (%s, %s, %s, %s)
ON DUPLICATE KEY UPDATE
    rate = VALUES(rate)
"""

# rate_date 형식 변환 (YYYYMMDD → YYYY-MM-DD)
rate_date = f"{data['date'][:4]}-{data['date'][4:6]}-{data['date'][6:8]}"

cursor.execute(sql, (
    data["base"],
    data["target"],
    data["rate"],
    rate_date,
))
conn.commit()
cursor.close()
conn.close()

print("✅ exchange_rate 테이블 업서트 완료")