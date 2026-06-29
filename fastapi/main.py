from fastapi import FastAPI
from routers import exchange, etf

app = FastAPI(
    title="미국 월배당 ETF 시뮬레이터 API",
    description="Polygon.io + 한국수출입은행 환율 기반 ETF 배당 정보 제공",
    version="1.0.0",
)

app.include_router(exchange.router)
app.include_router(etf.router)


@app.get("/")
def root():
    return {"message": "ETF 시뮬레이터 API 서버 정상 작동 중"}