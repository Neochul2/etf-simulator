<img width="754" height="1008" alt="image" src="https://github.com/user-attachments/assets/04e7f1b2-15a9-4f62-8e3d-20213f41a377" />


# 미국 월배당 ETF 시뮬레이터

Polygon.io 실제 데이터 기반 배당 계산 및 DRIP 복리 시뮬레이션 서비스

배포 URL: http://150.230.218.157:8080/etf-egov/etf/list.do

---

## 프로젝트 개요

월배당 ETF 투자 수요 증가에 대응하여, 미국 시장의 실제 ETF 데이터를 활용한 배당 계산 및 재투자(DRIP) 복리 시뮬레이션 웹 서비스를 구현하였습니다.
Spring MVC(eGovFrame)와 FastAPI를 연계하여 데이터 수집과 화면 서비스를 분리한 2-tier 구조로 설계하였습니다.

- 개발자: 정대철
- 개발 기간: 2026.06.22 ~ 2026.07.09
- 대상 ETF: ETFdb.com 기준 시가총액 상위 100개 월배당 ETF

---

## 주요 기능

| 기능 | 설명 |
|---|---|
| ETF 조회 | 티커 검색 및 자동완성, 현재가·OHLCV·배당률·최근 12개월 배당 내역 표시 |
| 월배당금 계산기 | 투자금액(원화) 입력시 세전·세후 월/연 배당금 자동 계산 (세율 15.4% 고정) |
| DRIP 복리 시뮬레이션 | 목돈 일시납/월 적립식 선택 후 1·3·5·10년 세후 총배당금·총자산 예측 |
| 내 포트폴리오 | 보유 ETF 등록·수정·삭제, 종목별·합계 세후 월/연 배당금 자동 계산 |
| 환율 자동 적용 | 한국수출입은행 API 기반 USD/KRW 환율 1일 1회 갱신, 모든 계산에 자동 반영 |
| 텔레그램 자동 알림 | crontab 실행 후 ETF 가격·배당·환율 업데이트 결과를 Telegram Bot으로 자동 알림 |

---

## 화면 구성

| 화면 | URL | 설명 |
|---|---|---|
| ETF 조회 | /etf/list.do | 종목 검색 -> 현재가·OHLCV·배당률·12개월 배당 내역 조회 |
| 배당금 계산기 | /etf/calculator.do | 투자금 입력 -> 세전/세후 월·연 배당금 계산 |
| DRIP 시뮬레이션 | /etf/simulator.do | 목돈/적립식 선택 -> 1·3·5·10년 복리 자산 예측 |
| 내 포트폴리오 | /etf/portfolio.do | 보유 ETF 등록·수정·삭제 -> 배당금 합산 관리 |

4개 화면 공통으로 티커 자동완성 드롭다운(방향키·Enter·ESC 키보드 지원)이 적용되어 있습니다.

---

## 기술 스택

| 구분 | 내용 |
|---|---|
| 언어 | Java 17 / Python 3.12.10 |
| 프레임워크 | eGovFrame 4.3.1 (Spring MVC) / FastAPI 0.115 |
| ORM | MyBatis 3.5.15 |
| 데이터베이스 | MySQL 8.0.46 |
| 배포 서버 | Oracle Cloud VM.Standard.E2.1.Micro (Ubuntu 24.04) |
| WAS | Apache Tomcat 9.0.86 / uvicorn + systemd |
| 개발 도구 | eGovFrame Eclipse / Visual Studio Code |
| 빌드 도구 | Maven |
| 외부 API | Polygon.io (주가·배당·배당락일) / 한국수출입은행 (USD/KRW 환율) |
| 자동화 | crontab + Telegram Bot API |
| 형상관리 | Git (feature -> develop -> main) |

---

## DB 구성

| 테이블 | 설명 |
|---|---|
| etf_info | ETF 기본정보 (현재가·OHLCV·issuer·description·배당률·배당횟수) |
| etf_dividend | 배당 내역 (배당락일·지급일·주당배당금 / ex_div_date 기준 unique) |
| exchange_rate | USD/KRW 환율 (rate_date 기준 unique) |
| simulation_result | DRIP 시뮬레이션 결과 저장 (투자조건·세후총배당금·총자산) |
| portfolio | 사용자 보유 ETF 및 투자금액(원화) |

---

## 아키텍처


<img width="1536" height="1024" alt="6ca9c47f-a3b6-4633-b203-862ce37b3583" src="https://github.com/user-attachments/assets/98d3af7a-40e0-449f-8d27-e48951265ac6" />



- FastAPI: 외부 API 데이터 수집·적재 및 REST API 제공 (crontab 매일 자동 실행)
- eGovFrame: FastAPI를 RestTemplate으로 호출, FastAPI 장애시 DB 직접 조회(fallback) 자동 전환

---

## 프로젝트 구조

```
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
│   ├── scripts/
│   │   ├── step0_load_exchange_rate.py   # 환율 최초 적재
│   │   ├── step1_load_etf_master.py      # ETF 마스터 100개 초기 적재
│   │   ├── step2_load_dividend.py        # 배당 내역 12개월치 초기 적재
│   │   ├── step3_load_descriptions.py    # ETF 설명 CSV 적재 (AI 생성 한국어)
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
        │   └── spring/                    # Spring 설정 XML
        └── webapp/WEB-INF/jsp/
            ├── etfList.jsp
            ├── calculator.jsp
            ├── simulation.jsp
            ├── portfolio.jsp
            └── error.jsp
```

---

## API 목록

### eGovFrame (Tomcat :8080)

| 메서드 | URL | 설명 |
|---|---|---|
| GET | /etf/list.do | ETF 조회 화면 진입 |
| GET | /etf/symbols.do | 시총 상위 100개 ETF 목록 (드롭다운용) |
| GET | /etf/{symbol}/detail.do | ETF 상세 + 배당 내역 조회 (FastAPI 우선, fallback 지원) |
| GET | /etf/calculator.do | 배당금 계산기 화면 진입 |
| GET | /etf/{symbol}/calculate.do | 세전·세후 월/연 배당금 계산 |
| GET | /etf/simulator.do | DRIP 시뮬레이션 화면 진입 |
| GET | /etf/{symbol}/simulate.do | DRIP 복리 시뮬레이션 계산 |
| POST | /etf/{symbol}/simulation/save.do | 시뮬레이션 결과 DB 저장 |
| GET | /etf/portfolio.do | 포트폴리오 화면 진입 |
| GET | /etf/portfolio/data.do | 보유 종목·세후 배당금 조회 |
| POST | /etf/portfolio/add.do | 종목 등록 |
| POST | /etf/portfolio/update.do | 투자금액 수정 |
| POST | /etf/portfolio/delete.do | 종목 삭제 |
| GET | /exchange/latest.do | 최신 USD/KRW 환율 조회 |
| POST | /exchange/update.do | 환율 최신화 (FastAPI 연계) |

### FastAPI (uvicorn :8000)

| 메서드 | URL | 설명 |
|---|---|---|
| GET | / | 서버 상태 확인 |
| GET | /etf/list | ETF 전체 목록 (배당률 내림차순) |
| GET | /etf/{ticker} | ETF 상세 조회 (camelCase 변환 반환) |
| GET | /etf/{ticker}/dividends | ETF 배당 내역 (최근 12개월) |
| GET | /exchange/usdkrw | 최신 USD/KRW 환율 조회 |
| POST | /exchange/update | 수출입은행 API 호출 -> 환율 DB 업서트 |

---

## 핵심 계산 로직 (Java)

### 배당금 계산기 (DividendCalculatorServiceImpl)

```
USD 환산 투자금    = 투자금(원) / 환율
세전 연배당금(USD) = 투자금(USD) * 배당률(%)
세후 연배당금(USD) = 세전 연배당금 * (1 - 0.154)   // 배당소득세 15.4%
세전/세후 월배당금 = 연배당금 / 12
원화 환산          = 각 USD 금액 * 환율
```

### DRIP 복리 시뮬레이션 (DripCalculatorServiceImpl)

```
월 세후 수익률 = (연배당률 / 12) * (1 - 0.154)

매월 반복:
  [적립식] 자산 += 월 추가 납입금
  월 배당금      = 자산 * 월 세후 수익률
  자산          += 월 배당금     // 배당 즉시 재투자 (DRIP)

수익률 = 누적배당금 / 실투자금 * 100
```

BigDecimal + MathContext(20) 사용으로 부동소수점 오차 없이 정밀 계산

### 포트폴리오 합산 (PortfolioServiceImpl)

portfolio LEFT JOIN etf_info 1회 쿼리로 N+1 문제 해결
각 ETF별 세후 월배당금·연배당금 계산 후 전체 합산 반환

---

## eGovFrame - FastAPI 연동 방식

```
EtfController       -> GET FastAPI /etf/{ticker}           -> EtfInfoVO (camelCase JSON)
                    -> GET FastAPI /etf/{ticker}/dividends  -> EtfDividendVO[] + KRW 환산
                    -> [FastAPI 장애시] MyBatis DB 직접 조회 (fallback 자동 전환)

CalculatorController -> GET FastAPI /etf/{ticker}           -> divYield 취득
                     -> GET FastAPI /exchange/usdkrw        -> 환율 취득
                     -> DividendCalculatorService.calculate() 호출

ExchangeController   -> POST FastAPI /exchange/update       -> 수출입은행 API 트리거
```

FastAPI polygon.py 응답 키는 Java VO 필드명(camelCase)과 일치하도록 변환되어 RestTemplate 역직렬화가 자동으로 동작합니다.
세후 배당이율(divYield * 0.846) 및 배당 내역 한화 환산(cashAmount * 환율)은 EtfController에서 계산하여 응답에 포함합니다.

---

## 설치 및 실행

### FastAPI 서버

```bash
cd fastapi
pip install -r requirements.txt
```

.env 파일 생성:

```
POLYGON_API_KEY=your_polygon_key
KOREAEXIM_API_KEY=your_koreaexim_key
DB_HOST=localhost
DB_PORT=3307
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=etf_db
TELEGRAM_TOKEN=your_telegram_bot_token
TELEGRAM_CHAT_ID=your_chat_id
```

```bash
# API 연결 테스트
python tests/test_polygon_connection.py
python tests/test_koreaexim_connection.py

# 초기 데이터 적재 (최초 1회 / data/ 폴더에 etfdb_screener CSV 필요)
python scripts/step0_load_exchange_rate.py
python scripts/step1_load_etf_master.py      # 약 40분 소요 (API rate limit 대응)
python scripts/step2_load_dividend.py
python scripts/step3_load_descriptions.py    # AI 생성 한국어 ETF 설명 CSV 적재

# 서버 실행
uvicorn main:app --host 0.0.0.0 --port 8000
```

### eGovFrame 서버

globals.properties에 FastAPI URL 설정:

```
fastapi.base.url=http://localhost:8000
```

Maven 빌드 후 Tomcat 배포:

```bash
mvn clean package
# target/etf-egov.war -> Tomcat webapps/
```

---

## 자동화 (crontab, KST 기준)

```
30 11 * * 1-5  cd /path/to/fastapi && python scripts/step5_update_exchange.py
40 11 * * 1-5  cd /path/to/fastapi && python scripts/step4_update_prices.py
```

수집 결과는 Telegram Bot으로 자동 알림 (성공·실패 종목 수 및 실패 종목 목록 포함)

---

