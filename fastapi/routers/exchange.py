"""
exchange.py — 환율 관련 엔드포인트
"""
from fastapi import APIRouter, HTTPException
from database import get_connection
from services.koreaexim import get_usdkrw

router = APIRouter(
    prefix="/exchange",
    tags=["환율"],
)

@router.get("/usdkrw")
def usdkrw():
    """USD/KRW 환율 조회 (DB에서 최신 데이터)"""
    try:
        conn = get_connection()
        cursor = conn.cursor(dictionary=True)
        cursor.execute("""
            SELECT base_cur, target_cur, rate, rate_date
            FROM exchange_rate
            ORDER BY rate_date DESC
            LIMIT 1
        """)
        row = cursor.fetchone()
        cursor.close()
        conn.close()
        if not row:
            raise Exception("환율 데이터 없음")
        return {
            "base":   row["base_cur"],
            "target": row["target_cur"],
            "rate":   float(row["rate"]),
            "date":   str(row["rate_date"]).replace("-", "")
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/update")
def update_exchange():
    """수출입은행 API 호출 → DB 업서트 → 최신 환율 반환"""
    try:
        rate_data = get_usdkrw()
        rate_date = f"{rate_data['date'][:4]}-{rate_data['date'][4:6]}-{rate_data['date'][6:8]}"

        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute("""
            INSERT INTO exchange_rate (base_cur, target_cur, rate, rate_date)
            VALUES (%s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE rate = VALUES(rate)
        """, (rate_data["base"], rate_data["target"], rate_data["rate"], rate_date))
        conn.commit()
        cursor.close()
        conn.close()

        return {
            "status": "ok",
            "rate":   rate_data["rate"],
            "date":   rate_data["date"]
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))