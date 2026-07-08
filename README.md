🇺🇸 미국 월배당 ETF 시뮬레이터


Polygon.io 실제 데이터 기반 배당 계산 · DRIP 복리 시뮬레이션 서비스



배포 URL: http://150.230.218.157:8080/etf-egov/etf/list.do


📌 프로젝트 개요

월배당 ETF 투자 수요 증가에 따라, 미국 시장의 실제 ETF 데이터를 활용한 배당 계산 및 재투자(DRIP) 복리 시뮬레이션 서비스를 구현하였습니다.


개발자: 정대철
개발 기간: 2026.06.22 ~ 2026.07.07
대상 ETF: ETFdb.com 기준 시총 상위 100개 월배당 ETF



🖥️ 화면 구성

화면URL설명ETF 조회/etf/list.do종목 검색 → 현재가·배당률·6개월 배당내역 조회배당금 계산기/etf/calculator.do투자금 입력 → 세전/세후 월·연 배당금 계산DRIP 시뮬레이션/etf/simulator.do목돈/적립식 선택 → 1·3·5·10년 복리 자산 예측내 포트폴리오/etf/portfolio.do보유 ETF 등록·수정·삭제 → 배당금 합산 관리


⚙️ 기술 스택

구분내용언어Java 17 / Python 3.12프레임워크eGovFrame 4.3.1 / FastAPI 0.115ORMMyBatis 3.5.15데이터베이스MySQL 8.0배포 서버Apache Tomcat 9.0 / uvicorn + systemd인프라Oracle Cloud VM (Ubuntu 24.04)외부 APIPolygon.io (주가·배당) / 한국수출입은행 (USD/KRW 환율)자동화crontab + Telegram Bot 알림형상관리Git (feature → develop → main)


🗄️ DB 구성

테이블주요 컬럼설명etf_infosymbol, price, open/high/low_price, volume, issuer, description, annual_div, div_yield, div_count, updated_atETF 기본정보 및 가격·배당률etf_dividendsymbol, ex_div_date, pay_date, cash_amount배당 내역 (ex_div_date 기준 unique)exchange_ratebase_cur, target_cur, rate, rate_dateUSD/KRW 환율 (rate_date 기준 unique)simulation_resultsymbol, initial_amount, monthly_amount, months, final_assets, total_dividendDRIP 시뮬레이션 결과 저장portfoliosymbol, invest_amt, created_at사용자 보유 ETF 및 투자금액


🔄 아키텍처

[ETFdb.com CSV]    [Polygon.io API]    [한국수출입은행 API]
       ↓                  ↓                     ↓
  step1 (초기)       step4 (매일)          step5 (매일)
       ↓                  ↓                     ↓
                  [MySQL 8.0.46]
                         ↓
         ┌───────────────┴───────────────┐
         │       FastAPI (REST API)       │
         │  /etf/* , /exchange/*         │
         └───────────────┬───────────────┘
                         ↓
            [eGovFrame + MyBatis (Tomcat)]
             Controller → Service → Mapper
                         ↓
                     [JSP 화면]


FastAPI: 외부 API 데이터 수집·적재 및 REST API 제공 (crontab 매일 자동 실행)
eGovFrame: FastAPI REST API 호출 → 비즈니스 로직 처리 → JSP 화면 서비스



🗂️ 프로젝트 구조

etf-simulator/
├── fastapi/                              # Python 데이터 수집 서버
│   ├── main.py                           # FastAPI 앱 진입점
│   ├── config.py                         # 환경변수 로드 (.env 기반)
│   ├── database.py                       # MySQL 연결 관리
│   ├── requirements.txt
│   ├── routers/
│   │   ├── etf.py                        # /etf/* 엔드포인트
│   │   └── exchange.py                   # /exchange/* 엔드포인트
│   ├── services/
│   │   ├── polygon.py                    # Polygon.io API 연동 + DB 조회
│   │   └── koreaexim.py                  # 한국수출입은행 환율 API
│   ├── scripts/                          # 데이터 적재·갱신 스크립트
│   │   ├── step0_load_exchange_rate.py   # 환율 최초 적재
│   │   ├── step1_load_etf_master.py      # ETF 마스터 100개 초기 적재
│   │   ├── step2_load_dividend.py        # 배당 내역 12개월치 초기 적재
│   │   ├── step3_load_descriptions.py    # ETF 설명 CSV 적재
│   │   ├── step4_update_prices.py        # 매일 가격·배당 갱신 (cron)
│   │   ├── step5_update_exchange.py      # 매일 환율 갱신 (cron)
│   │   └── telegram_notify.py            # Telegram 알림 유틸
│   └── tests/
│       ├── test_polygon_connection.py
│       ├── test_koreaexim_connection.py
│       └── verify_top100.py              # DB 적재 종목 검증
│
└── etf-egov/                             # Java 화면 서버 (eGovFrame)
    └── src/main/
        ├── java/com/kopo/etf/
        │   ├── controller/
        │   │   ├── EtfController.java        # ETF 조회·상세 (화면1)
        │   │   ├── CalculatorController.java  # 배당금 계산기 (화면2)
        │   │   ├── SimulationController.java  # DRIP 시뮬레이션 (화면3)
        │   │   ├── PortfolioController.java   # 포트폴리오 관리 (화면4)
        │   │   └── ExchangeController.java    # 환율 조회·갱신
        │   ├── service/impl/
        │   │   ├── DividendCalculatorServiceImpl.java  # 세전/세후 배당금 계산
        │   │   ├── DripCalculatorServiceImpl.java      # DRIP 복리 시뮬레이션
        │   │   ├── PortfolioServiceImpl.java           # 포트폴리오 배당 합산
        │   │   ├── EtfInfoServiceImpl.java
        │   │   ├── ExchangeRateServiceImpl.java
        │   │   └── SimulationResultServiceImpl.java
        │   ├── mapper/                    # MyBatis Mapper 인터페이스
        │   └── vo/                        # EtfInfoVO, DripResultVO 등
        ├── resources/egovframework/
        │   ├── mapper/                    # MyBatis SQL XML
        │   │   ├── etf-info-mapper.xml
        │   │   ├── etf-dividend-mapper.xml
        │   │   ├── exchange-rate-mapper.xml
        │   │   ├── portfolio-mapper.xml
        │   │   └── simulation-result-mapper.xml
        │   └── spring/                    # Spring 설정 XML
        │       ├── context-datasource.xml
        │       ├── context-mapper.xml
        │       ├── context-transaction.xml
        │       └── context-common.xml
        └── webapp/WEB-INF/jsp/
            ├── etfList.jsp
            ├── calculator.jsp
            ├── simulation.jsp
            ├── portfolio.jsp
            └── error.jsp


📡 FastAPI 엔드포인트

메서드경로설명GET/서버 상태 확인GET/etf/listETF 전체 목록 조회 (배당률 내림차순)GET/etf/{ticker}ETF 상세 조회 (가격·OHLCV·배당률·설명)GET/etf/{ticker}/dividendsETF 배당 내역 조회 (최근 12개월)GET/exchange/usdkrw최신 USD/KRW 환율 조회POST/exchange/update수출입은행 API 호출 → 환율 DB 업서트


🔢 핵심 계산 로직 (Java)

배당금 계산기 (DividendCalculatorServiceImpl)

USD 환산 투자금    = 투자금(원) ÷ 환율
세전 연배당금(USD) = 투자금(USD) × 배당률(%)
세후 연배당금(USD) = 세전 연배당금 × (1 − 0.154)   ← 배당소득세 15.4%
세전/세후 월배당금 = 연배당금 ÷ 12
원화 환산          = 각 USD 금액 × 환율

DRIP 복리 시뮬레이션 (DripCalculatorServiceImpl)

월 세후 수익률 = (연배당률 ÷ 12) × (1 − 0.154)

매월 반복:
  [적립식] 자산 += 월 추가 납입금
  월 배당금    = 자산 × 월 세후 수익률
  자산        += 월 배당금   ← 배당 즉시 재투자 (DRIP)

수익률 = 누적배당금 ÷ 실투자금 × 100


BigDecimal + MathContext(20) 사용으로 부동소수점 오차 없이 정밀 계산



포트폴리오 합산 (PortfolioServiceImpl)


portfolio LEFT JOIN etf_info 1회 쿼리로 N+1 문제 해결
각 ETF별 세후 월배당금·연배당금 계산 후 전체 합산 반환



🔁 eGovFrame ↔ FastAPI 연동 방식

eGovFrame 컨트롤러는 RestTemplate으로 FastAPI를 호출하며, FastAPI 장애 시 DB에서 직접 조회하는 fallback 로직이 적용되어 있습니다.

EtfController → FastAPI /etf/{ticker}       → EtfInfoVO (camelCase JSON)
             → FastAPI /etf/{ticker}/dividends → EtfDividendVO[]
             → [실패 시] MyBatis DB 직접 조회 (fallback)

CalculatorController → FastAPI /etf/{ticker}     → divYield 취득
                     → FastAPI /exchange/usdkrw  → 환율 취득
                     → DividendCalculatorService.calculate() 호출

ExchangeController → POST FastAPI /exchange/update → 수출입은행 API 트리거


FastAPI polygon.py의 응답 키는 Java VO 필드명(camelCase)과 일치하도록 변환하여 RestTemplate 역직렬화가 자동으로 동작합니다.




🚀 설치 및 실행

FastAPI 서버

bashcd fastapi
pip install -r requirements.txt

.env 파일 생성:

envPOLYGON_API_KEY=your_polygon_key
KOREAEXIM_API_KEY=your_koreaexim_key
DB_HOST=localhost
DB_PORT=3307
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=etf_db
TELEGRAM_TOKEN=your_telegram_bot_token
TELEGRAM_CHAT_ID=your_chat_id

bash# API 연결 테스트
python tests/test_polygon_connection.py
python tests/test_koreaexim_connection.py

# 초기 데이터 적재 (최초 1회, data/ 폴더에 CSV 필요)
python scripts/step0_load_exchange_rate.py
python scripts/step1_load_etf_master.py      # 약 40분 소요 (API rate limit 대응)
python scripts/step2_load_dividend.py
python scripts/step3_load_descriptions.py

# 서버 실행
uvicorn main:app --host 0.0.0.0 --port 8000

eGovFrame 서버

globals.properties에 FastAPI URL 설정:

propertiesfastapi.base.url=http://localhost:8000

Maven 빌드 후 Tomcat에 배포:

bashmvn clean package
# target/etf-egov.war → Tomcat webapps/


⏰ 자동화 (crontab, KST 기준)

cron30 11 * * 1-5  cd /path/to/fastapi && python scripts/step5_update_exchange.py
40 11 * * 1-5  cd /path/to/fastapi && python scripts/step4_update_prices.py

수집 결과는 Telegram Bot으로 자동 알림 (성공/실패 종목 수, 실패 종목 목록 포함).


💡 핵심 구현 포인트


BigDecimal 정밀 계산: 세후 15.4% 배당소득세·DRIP 복리를 MathContext(20)으로 부동소수점 오차 없이 처리
FastAPI fallback: FastAPI 장애 시 eGovFrame이 MyBatis로 DB를 직접 조회하여 무중단 서비스
증분 업서트: 모든 적재 스크립트에 ON DUPLICATE KEY UPDATE 적용으로 중복 없는 안전한 갱신
N+1 쿼리 개선: 포트폴리오 목록을 portfolio LEFT JOIN etf_info 1회 쿼리로 최적화
API Rate Limit 대응: Polygon.io Free tier(5 req/min) 맞춰 호출 간 12~13초 대기
환율 영업일 자동 탐색: 수출입은행 API 최근 10일 역순 탐색으로 공휴일·주말 자동 처리
camelCase 변환: FastAPI 응답 키를 Java VO 필드명과 일치시켜 RestTemplate 자동 역직렬화
환율 오차 방지: KRW→USD 환산 후 역산 시 발생하는 원화 오차를 컨트롤러에서 원본 KRW 값으로 직접 보정
