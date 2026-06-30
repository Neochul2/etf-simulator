"""
update_assets.py — CSV의 시가총액(Assets) 값을 etf_info.assets 컬럼에 업데이트
실행: python scripts/update_assets.py
"""
import pandas as pd
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from database import get_connection

CSV_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "etfdb_screener_6.29.csv")


def parse_assets(val):
    try:
        return int(float(str(val).replace("$", "").replace(",", "")))
    except:
        return 0


# 1. CSV 로드
df = pd.read_csv(CSV_PATH, header=1)
df.columns = df.columns.str.strip()
df["assets_num"] = df["Assets"].apply(parse_assets)

# 2. DB 연결
conn = get_connection()
cursor = conn.cursor()

# 3. DB에 있는 티커만 업데이트
cursor.execute("SELECT symbol FROM etf_info")
db_tickers = set(row[0] for row in cursor.fetchall())

updated = 0
for _, row in df.iterrows():
    ticker = row["Symbol"]
    if ticker in db_tickers:
        cursor.execute(
            "UPDATE etf_info SET assets = %s WHERE symbol = %s",
            (row["assets_num"], ticker)
        )
        updated += 1

conn.commit()
cursor.close()
conn.close()

print(f"완료 — {updated}개 종목 시가총액 업데이트")