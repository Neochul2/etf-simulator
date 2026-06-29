"""
exchange.py — 환율 관련 엔드포인트
"""
from fastapi import APIRouter, HTTPException
from services.koreaexim import get_usdkrw

router = APIRouter(
    prefix="/exchange",
    tags=["환율"],
)


@router.get("/usdkrw")
def usdkrw():
    """USD/KRW 환율 조회"""
    try:
        return get_usdkrw()
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))