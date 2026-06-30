"""
etf.py — ETF 관련 엔드포인트
"""
from fastapi import APIRouter, HTTPException
from database import get_connection
from services.polygon import get_etf_list, get_etf_detail, get_etf_dividends

router = APIRouter(
    prefix="/etf",
    tags=["ETF"],
)


@router.get("/list")
def etf_list():
    """ETF 목록 전체 조회"""
    try:
        conn = get_connection()
        result = get_etf_list(conn)
        conn.close()
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{ticker}")
def etf_detail(ticker: str):
    """ETF 상세 조회"""
    try:
        conn = get_connection()
        result = get_etf_detail(ticker, conn)
        conn.close()
        if not result:
            raise HTTPException(status_code=404, detail=f"{ticker} 종목을 찾을 수 없습니다.")
        return result
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{ticker}/dividends")
def etf_dividends(ticker: str):
    """ETF 배당 내역 조회 (DB 기준)"""
    try:
        conn = get_connection()
        result = get_etf_dividends(ticker, conn)
        conn.close()
        if not result:
            raise HTTPException(status_code=404, detail=f"{ticker} 배당 내역을 찾을 수 없습니다.")
        return result
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))