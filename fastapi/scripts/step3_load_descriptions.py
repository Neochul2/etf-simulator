"""
step4_load_descriptions.py — ETF 설명 CSV를 읽어서 etf_info.description 컬럼에 UPDATE
실행: python scripts/step4_load_descriptions.py
"""
import csv
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database import get_connection

# CSV 경로 (fastapi/ 폴더 기준)
CSV_PATH = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'etf_descriptions.csv')

conn = get_connection()
cursor = conn.cursor()

success = 0
failed = []

with open(CSV_PATH, encoding='utf-8') as f:
    reader = csv.DictReader(f)
    for row in reader:
        symbol = row['symbol'].strip()
        description = row['description'].strip()
        try:
            cursor.execute("""
                UPDATE etf_info SET description = %s WHERE symbol = %s
            """, (description, symbol))
            conn.commit()
            success += 1
            print(f"✅ {symbol:<8} 적재 완료")
        except Exception as e:
            print(f"❌ {symbol:<8} 오류: {e}")
            failed.append(symbol)

cursor.close()
conn.close()

print(f"\n완료 — 성공: {success}개 / 실패: {len(failed)}개")
if failed:
    print(f"실패 종목: {failed}")