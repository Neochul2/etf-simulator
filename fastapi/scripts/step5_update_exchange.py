"""
step5_update_exchange.py — 한국수출입은행 API에서 USD/KRW 환율 조회 → 업서트
실행: python scripts/step5_update_exchange.py
"""
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from services.koreaexim import get_usdkrw
from database import get_connection
from telegram_notify import notify_failure

try:
    conn = get_connection()
    cursor = conn.cursor()

    print("환율 조회 중...")
    rate_data = get_usdkrw()
    rate_date = f"{rate_data['date'][:4]}-{rate_data['date'][4:6]}-{rate_data['date'][6:8]}"

    cursor.execute("""
        INSERT INTO exchange_rate (base_cur, target_cur, rate, rate_date)
        VALUES (%s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE rate = VALUES(rate)
    """, (rate_data["base"], rate_data["target"], rate_data["rate"], rate_date))
    conn.commit()

    print(f"✅ USD/KRW = {rate_data['rate']} (기준일: {rate_data['date']}) 업서트 완료")

    # 텔레그램 알림
    from telegram_notify import send_message
    from datetime import datetime
    send_message(
        f"💱 <b>환율 업데이트 완료</b>\n"
        f"📅 {datetime.now().strftime('%Y-%m-%d %H:%M')}\n"
        f"💵 USD/KRW = {rate_data['rate']:,.1f}"
    )

except Exception as e:
    print(f"❌ 환율 적재 실패: {e}")
    notify_failure(f"환율 업데이트 실패: {e}")

finally:
    cursor.close()
    conn.close()