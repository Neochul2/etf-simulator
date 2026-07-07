"""
koreaexim.py — 한국수출입은행 환율 API 서비스
"""
import requests
from datetime import datetime, timedelta
from config import KOREAEXIM_API_KEY


def get_usdkrw() -> dict:
    """USD/KRW 환율 조회 (최근 영업일 자동 탐색)"""
    for i in range(10):
        date_str = (datetime.now() - timedelta(days=i)).strftime("%Y%m%d")
        try:
            r = requests.get(
                "https://oapi.koreaexim.go.kr/site/program/financial/exchangeJSON",
                params={
                    "authkey": KOREAEXIM_API_KEY,
                    "searchdate": date_str,
                    "data": "AP01",
                },
                timeout=3,
                verify=False,
            )
            data = r.json()
        except requests.exceptions.RequestException:
            continue

        for item in data:
            if item.get("cur_unit") == "USD":
                return {
                    "base": "USD",
                    "target": "KRW",
                    "rate": float(item["deal_bas_r"].replace(",", "")),
                    "date": date_str,
                }
    raise Exception("환율 데이터를 가져올 수 없습니다.")