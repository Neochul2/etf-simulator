import os
import requests
from datetime import datetime
from dotenv import load_dotenv

load_dotenv()

TELEGRAM_TOKEN   = os.getenv("TELEGRAM_TOKEN")
TELEGRAM_CHAT_ID = os.getenv("TELEGRAM_CHAT_ID")


def send_message(message: str):
    if not TELEGRAM_TOKEN or not TELEGRAM_CHAT_ID:
        print("텔레그램 설정 없음 — 전송 생략")
        return
    url = f"https://api.telegram.org/bot{TELEGRAM_TOKEN}/sendMessage"
    try:
        res = requests.post(url, data={
            "chat_id": TELEGRAM_CHAT_ID,
            "text": message,
            "parse_mode": "HTML"
        }, timeout=10)
        print("텔레그램 전송 성공" if res.status_code == 200 else f"실패: {res.text}")
    except Exception as e:
        print(f"텔레그램 전송 오류: {e}")


def notify_success(etf_count: int, failed_count: int = 0, failed_list: list = None):
    msg = (
        f"✅ <b>ETF 가격/배당 업데이트 완료</b>\n"
        f"📅 {datetime.now().strftime('%Y-%m-%d %H:%M')}\n"
        f"📊 성공: {etf_count}개"
    )
    if failed_count > 0:
        msg += f"\n⚠️ 실패: {failed_count}개 {failed_list or ''}"
    send_message(msg)


def notify_failure(error: str):
    send_message(
        f"❌ <b>ETF 데이터 업데이트 실패</b>\n"
        f"📅 {datetime.now().strftime('%Y-%m-%d %H:%M')}\n"
        f"⚠️ 오류: {error}"
    )


if __name__ == "__main__":
    send_message("🧪 ETF 시뮬레이터 텔레그램 연동 테스트!")