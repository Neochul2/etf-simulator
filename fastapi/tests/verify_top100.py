"""
verify_top100.py — etf_info에 적재된 100개가 CSV 기준 시총 상위 100개와 일치하는지 검증
실행: python scripts/verify_top100.py
"""
import pandas as pd
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from database import get_connection

CSV_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "etfdb_screener_6.29.csv")
TOP_N = 100


def parse_assets(val):
    try:
        return int(float(str(val).replace("$", "").replace(",", "")))
    except:
        return 0


# 1. CSV에서 시총 상위 100개 다시 추출
df = pd.read_csv(CSV_PATH, header=1)
df.columns = df.columns.str.strip()
df["assets_num"] = df["Assets"].apply(parse_assets)
top100_csv = df.sort_values("assets_num", ascending=False).head(TOP_N)
csv_tickers = set(top100_csv["Symbol"].tolist())

print(f"CSV 기준 시총 상위 {TOP_N}개 추출 완료 ({len(csv_tickers)}개)")

# 2. DB에서 적재된 티커 + 시총 가져오기
conn = get_connection()
cursor = conn.cursor()
cursor.execute("SELECT symbol, assets FROM etf_info")
db_rows = cursor.fetchall()
db_tickers = set(row[0] for row in db_rows)
db_assets = {row[0]: row[1] for row in db_rows}
conn.close()

print(f"DB 적재 종목 수: {len(db_tickers)}개")

# 3. 티커 일치 여부 비교
only_in_csv = csv_tickers - db_tickers
only_in_db = db_tickers - csv_tickers

print("\n" + "=" * 50)
print("1) 종목 일치 검증")
print("=" * 50)

if not only_in_csv and not only_in_db:
    print("✅ 완전히 일치합니다.")
else:
    if only_in_csv:
        print(f"❌ CSV에는 있지만 DB에 없는 종목 ({len(only_in_csv)}개): {only_in_csv}")
    if only_in_db:
        print(f"❌ DB에는 있지만 CSV 상위 100위 밖인 종목 ({len(only_in_db)}개): {only_in_db}")

# 4. 시총 값 일치 여부 비교 (assets 컬럼이 있는 경우)
print("\n" + "=" * 50)
print("2) 시가총액 값 일치 검증")
print("=" * 50)

csv_assets_map = dict(zip(top100_csv["Symbol"], top100_csv["assets_num"]))
mismatch = []

for ticker in (csv_tickers & db_tickers):
    csv_val = csv_assets_map.get(ticker, 0)
    db_val = db_assets.get(ticker, 0) or 0
    if csv_val != db_val:
        mismatch.append((ticker, csv_val, db_val))

if not mismatch:
    print("✅ 시가총액 값도 전부 일치합니다.")
else:
    print(f"❌ 시가총액 불일치 종목 ({len(mismatch)}개):")
    for ticker, csv_val, db_val in mismatch:
        print(f"  {ticker:<8} CSV: ${csv_val:,} | DB: ${db_val:,}")

print("=" * 50)