import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from database import get_connection

conn = get_connection()
cursor = conn.cursor()
cursor.execute('SELECT symbol, issuer FROM etf_info ORDER BY symbol')
rows = cursor.fetchall()
for r in rows:
    print(f'{r[0]},{r[1]}')
conn.close()