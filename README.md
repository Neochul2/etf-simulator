# 미국 월배당 ETF 시뮬레이터
<img width="604" height="791" alt="image" src="https://github.com/user-attachments/assets/8e58b09a-f738-41c7-9e5d-e03587e04c5e" />


> Polygon.io 실제 데이터 기반 배당 계산 및 DRIP 복리 시뮬레이션 서비스

배포 URL: http://150.230.218.157:8080/etf-egov/etf/list.do

---

## 프로젝트 개요

월배당 ETF 투자 수요 증가에 대응하여, 미국 시장의 실제 ETF 데이터를 활용한 배당 계산 및 재투자(DRIP) 복리 시뮬레이션 웹 서비스를 구현하였습니다.
FastAPI(Python)가 외부 API로부터 데이터를 수집·적재하고, eGovFrame(Java Spring MVC)이 FastAPI를 호출하여 화면을 서비스하는 2-tier 구조로 설계하였습니다.

- **개발자**: 정대철
- **개발 기간**: 2026.06.22 ~ 2026.07.09
- **대상 ETF**: ETFdb.com 기준 시가총액 상위 100개 월배당 ETF

---

## 주요 기능

| 기능 | 설명 |
|---|---|
| ETF 조회 | 티커 검색 및 자동완성(방향키·Enter·ESC 지원), 현재가·OHLCV·배당률·최근 12개월 배당 내역 표시 |
| 월배당금 계산기 | 투자금액(원화) 입력 시 세전·세후 월/연 배당금 자동 계산 (배당소득세 15.4% 고정) |
| DRIP 복리 시뮬레이션 | 목돈 일시납 / 월 적립식 선택 후 1·3·5·10년 세후 총배당금·총자산 예측, 결과 DB 저장 |
| 내 포트폴리오 | 보유 ETF 등록·수정·삭제, 종목별 및 합계 세후 월/연 배당금 자동 계산 |
| 환율 자동 적용 | 한국수출입은행 API 기반 USD/KRW 환율 1일 1회 자동 갱신, 모든 계산에 반영 |
| 텔레그램 알림 | crontab 실행 후 ETF 가격·배당·환율 업데이트 결과를 Telegram Bot으로 자동 전송 |

---

## 화면 구성

| 화면 | URL | 설명 |
|---|---|---|
| ETF 조회 | `/etf/list.do` | 티커 검색 → 현재가·OHLCV·배당률·12개월 배당 내역 조회 |
| 배당금 계산기 | `/etf/calculator.do` | 투자금 입력 → 세전/세후 월·연 배당금 계산 |
| DRIP 시뮬레이션 | `/etf/simulator.do` | 목돈/적립식 선택 → 1·3·5·10년 복리 자산 예측 |
| 내 포트폴리오 | `/etf/portfolio.do` | 보유 ETF 등록·수정·삭제 → 배당금 합산 관리 |

4개 화면 공통으로 티커 자동완성 드롭다운이 적용되어 있습니다.

---

## 기술 스택

| 구분 | 내용 |
|---|---|
| 언어 | Java 17 / Python 3.12 |
| 데이터베이스 | MySQL 8.0.46 (`etf_db`) |
| OS | Ubuntu 24.04 LTS / Windows 11 |
| 외부 API | Polygon.io (주가·배당·배당락일) / 한국수출입은행 (USD/KRW 환율) |
| 자동화 | crontab + Telegram Bot API |
| 개발 도구 | eGovFrame Eclipse / Visual Studio Code |

---

## 배포 환경

| 항목 | 내용 |
|---|---|
| 서버 | Oracle Cloud VM.Standard.E2.1.Micro |
| OS | Ubuntu 24.04 LTS |
| IP | 150.230.218.157 |
| DB | MySQL 8.0.46 (서버 내부 설치, DB명: `etf_db`, 포트: 3306) |
| FastAPI | uvicorn + systemd (포트 8000) |
| WAS | Apache Tomcat 9.0.86 (포트 8080, context: `/etf-egov`) |

### systemd 자동 기동

```bash
sudo systemctl enable mysql
sudo systemctl enable fastapi
sudo systemctl enable tomcat
```

---

## 프로젝트 구조

```
etf-simulator/
├── fastapi/                              # Python 데이터 수집 서버
│   ├── main.py                           # FastAPI 앱 진입점 (라우터 등록)
│   ├── config.py                         # .env 기반 환경변수 로드 (API 키, DB 접속정보)
│   ├── database.py                       # MySQL 연결 관리 (mysql-connector-python)
│   ├── requirements.txt                  # Python 패키지 목록
│   ├── routers/
│   │   ├── etf.py                        # GET /etf/list, /etf/{ticker}, /etf/{ticker}/dividends
│   │   └── exchange.py                   # GET /exchange/usdkrw, POST /exchange/update
│   ├── services/
│   │   ├── polygon.py                    # DB 조회 + camelCase 변환 반환 (Polygon.io 연동)
│   │   └── koreaexim.py                  # 수출입은행 API 호출, 최근 영업일 자동 탐색 (최대 10일)
│   ├── scripts/
│   │   ├── step0_load_exchange_rate.py   # 환율 최초 1회 적재
│   │   ├── step1_load_etf_master.py      # ETFdb CSV → 시총 상위 100개 추출 → Polygon 가격·배당 적재
│   │   ├── step2_load_dividend.py        # 100개 ETF 배당 내역 12개월치 초기 적재
│   │   ├── step3_load_descriptions.py    # ETF 설명 CSV 적재 (AI 생성 한국어 설명)
│   │   ├── step4_update_prices.py        # 매일 가격·배당 갱신 (cron, Telegram 알림 포함)
│   │   ├── step5_update_exchange.py      # 매일 환율 갱신 (cron)
│   │   └── telegram_notify.py            # Telegram Bot 알림 유틸 (성공/실패 메시지 전송)
│   └── tests/
│       ├── test_polygon_connection.py    # Polygon.io API 연결 테스트
│       ├── test_koreaexim_connection.py  # 수출입은행 API 연결 테스트
│       └── verify_top100.py             # DB 적재 100개 종목 검증
│
└── etf-egov/                             # Java 화면 서버 (eGovFrame / Spring MVC)
    └── src/main/
        ├── java/com/kopo/etf/
        │   ├── info/
        │   │   ├── controller/EtfController.java          # ETF 조회·상세 (FastAPI 우선 + DB fallback)
        │   │   ├── service/EtfInfoServiceImpl.java        # etfInfoMapper, etfDividendMapper 호출
        │   │   ├── mapper/EtfInfoMapper.java              # selectEtfInfo, selectAllEtfList
        │   │   ├── mapper/EtfDividendMapper.java          # selectRecentDividends (최근 12건)
        │   │   └── vo/EtfInfoVO.java                      # symbol, price, OHLCV, divYield, afterTaxYield 등
        │   ├── calculator/
        │   │   ├── controller/CalculatorController.java   # 배당금 계산기 (FastAPI→divYield·환율 취득)
        │   │   ├── service/DividendCalculatorServiceImpl.java  # BigDecimal 세전·세후 배당금 계산
        │   │   └── vo/CalculatorResultVO.java             # USD·KRW 세전/세후 월·연배당금 8개 필드
        │   ├── simulation/
        │   │   ├── controller/SimulationController.java   # DRIP 시뮬레이션 + 결과 저장
        │   │   ├── service/DripCalculatorServiceImpl.java # 월 복리 루프 계산 (BigDecimal MathContext(20))
        │   │   ├── service/SimulationResultServiceImpl.java  # 결과 INSERT
        │   │   ├── mapper/SimulationResultMapper.java     # insertSimulationResult (useGeneratedKeys)
        │   │   └── vo/DripResultVO.java                   # 총배당·총자산·투자금·수익률 (USD+KRW)
        │   ├── portfolio/
        │   │   ├── controller/PortfolioController.java    # 포트폴리오 CRUD (Ajax)
        │   │   ├── service/PortfolioServiceImpl.java      # portfolio LEFT JOIN etf_info 단일 쿼리
        │   │   ├── mapper/PortfolioMapper.java            # selectPortfolioList, insert, update, delete
        │   │   └── vo/PortfolioVO.java                    # symbol, investAmt, monthlyDiv, yearlyDiv 등
        │   └── exchange/
        │       ├── controller/ExchangeController.java     # GET /exchange/latest.do, POST /exchange/update.do
        │       ├── service/ExchangeRateServiceImpl.java   # exchangeRateMapper 호출
        │       ├── mapper/ExchangeRateMapper.java         # selectLatestRate
        │       └── vo/ExchangeRateVO.java
        ├── resources/egovframework/
        │   ├── egovProps/globals.properties               # fastapi.base.url=http://localhost:8000
        │   ├── mapper/
        │   │   ├── etf-info-mapper.xml                    # selectEtfInfo, selectAllEtfList
        │   │   ├── etf-dividend-mapper.xml                # selectRecentDividends (LIMIT 12)
        │   │   ├── exchange-rate-mapper.xml               # selectLatestRate
        │   │   ├── portfolio-mapper.xml                   # portfolio LEFT JOIN etf_info
        │   │   └── simulation-result-mapper.xml           # insertSimulationResult
        │   └── spring/
        │       ├── context-common.xml                     # component-scan (com.kopo.etf), globals.properties
        │       ├── context-datasource.xml                 # DriverManagerDataSource (MySQL localhost:3306)
        │       ├── context-mapper.xml                     # MapperScannerConfigurer (MyBatis)
        │       └── context-transaction.xml                # DataSourceTransactionManager
        └── webapp/
            ├── index.jsp                                  # /etf/list.do 리다이렉트
            ├── bootstrap/                                 # Bootstrap CSS·JS (로컬 번들)
            ├── js/jquery.min.js
            └── WEB-INF/
                ├── web.xml                               # DispatcherServlet (*.do), UTF-8 필터, 오류 페이지
                ├── config/.../dispatcher-servlet.xml    # component-scan, Jackson 날짜포맷, ViewResolver
                └── jsp/
                    ├── etfList.jsp
                    ├── calculator.jsp
                    ├── simulation.jsp
                    ├── portfolio.jsp
                    └── error.jsp
```

---

## 아키텍처
<img width="990" height="656" alt="image" src="https://github.com/user-attachments/assets/049147e4-3ea9-41a6-b327-efb35d182769" />

```
[외부 API]                   [FastAPI :8000]              [MySQL etf_db]
Polygon.io ──────────────→  services/polygon.py    ←──→  etf_info
수출입은행 API ─────────────→  services/koreaexim.py ←──→  etf_dividend
                                                    ←──→  exchange_rate
[crontab KST]
08:00 (월~토)  → step4_update_prices.py  → Polygon 가격·배당 갱신 → Telegram 알림
11:05 (평일)   → step5_update_exchange.py → 환율 갱신

[브라우저]
  │
  ▼
[eGovFrame / Tomcat :8080]
  EtfController          → RestTemplate → FastAPI /etf/{ticker}          → EtfInfoVO
                         → RestTemplate → FastAPI /etf/{ticker}/dividends → EtfDividendVO[]
                         → [FastAPI 장애 시] MyBatis DB 직접 조회 (자동 fallback)
  CalculatorController   → RestTemplate → FastAPI /etf/{ticker}           → divYield
                         → RestTemplate → FastAPI /exchange/usdkrw         → 환율
                         → DividendCalculatorService.calculate()
  SimulationController   → MyBatis etfInfoService.getEtfInfo()            → divYield
                         → DripCalculatorService.simulate()
                         → SimulationResultService.saveSimulationResult()
  PortfolioController    → PortfolioService.getPortfolioData()
                           (portfolio LEFT JOIN etf_info 단일 쿼리)
  ExchangeController     → RestTemplate → FastAPI POST /exchange/update    → 수출입은행 API 트리거
```

FastAPI `polygon.py`의 응답 키는 Java VO 필드명(camelCase)과 일치하도록 변환되어, RestTemplate의 역직렬화(`EtfInfoVO.class`)가 자동으로 동작합니다.
세후 배당이율(`divYield × 0.846`) 및 배당 내역 한화 환산(`cashAmount × 환율`)은 `EtfController`에서 처리합니다.

---

## DB 테이블 구성

| 테이블 | 주요 컬럼 | 설명 |
|---|---|---|
| `etf_info` | symbol(PK), price, open/high/low_price, volume, issuer, description, annual_div, div_yield, div_count, updated_at | ETF 기본정보 및 현재가·OHLCV |
| `etf_dividend` | symbol, ex_div_date(UNIQUE), pay_date, cash_amount | 배당 내역 (배당락일 기준 UNIQUE) |
| `exchange_rate` | base_cur, target_cur, rate, rate_date(UNIQUE) | USD/KRW 환율 (날짜 기준 UNIQUE) |
| `simulation_result` | symbol, invest_type, initial_amt, monthly_amt, months, div_yield, total_div, final_assets | DRIP 시뮬레이션 결과 저장 |
| `portfolio` | id(PK), symbol, invest_amt, created_at | 사용자 보유 ETF 및 투자금액(원화) |

---

## API 목록

### eGovFrame (Tomcat :8080, context: `/etf-egov`)

| 메서드 | URL | 설명 |
|---|---|---|
| GET | `/etf/list.do` | ETF 조회 화면 |
| GET | `/etf/symbols.do` | 전체 ETF 목록 (드롭다운용, 배당률 내림차순) |
| GET | `/etf/{symbol}/detail.do` | ETF 상세 + 배당 내역 (FastAPI 우선, DB fallback) |
| GET | `/etf/calculator.do` | 배당금 계산기 화면 |
| GET | `/etf/{symbol}/calculate.do` | 세전·세후 월/연 배당금 계산 |
| GET | `/etf/simulator.do` | DRIP 시뮬레이션 화면 |
| GET | `/etf/{symbol}/simulate.do` | DRIP 복리 시뮬레이션 계산 |
| POST | `/etf/{symbol}/simulation/save.do` | 시뮬레이션 결과 DB 저장 |
| GET | `/etf/portfolio.do` | 포트폴리오 화면 |
| GET | `/etf/portfolio/data.do` | 보유 종목·세후 배당금 조회 (Ajax) |
| POST | `/etf/portfolio/add.do` | 종목 등록 |
| POST | `/etf/portfolio/update.do` | 투자금액 수정 |
| POST | `/etf/portfolio/delete.do` | 종목 삭제 |
| GET | `/exchange/latest.do` | 최신 USD/KRW 환율 조회 |
| POST | `/exchange/update.do` | 환율 최신화 (FastAPI /exchange/update 트리거) |

### FastAPI (uvicorn :8000)

| 메서드 | URL | 설명 |
|---|---|---|
| GET | `/` | 서버 상태 확인 |
| GET | `/etf/list` | ETF 전체 목록 (배당률 내림차순) |
| GET | `/etf/{ticker}` | ETF 상세 조회 (camelCase 변환 반환) |
| GET | `/etf/{ticker}/dividends` | ETF 배당 내역 (DB 기준 최신순 전체) |
| GET | `/exchange/usdkrw` | 최신 USD/KRW 환율 (id DESC LIMIT 1) |
| POST | `/exchange/update` | 수출입은행 API 호출 → 환율 DB 업서트 |

---

## 핵심 계산 로직

모든 금액 계산은 `BigDecimal` + `MathContext(20, RoundingMode.HALF_UP)`을 사용하여 부동소수점 오차를 제거합니다.

### 배당금 계산기 (`DividendCalculatorServiceImpl`)

```
USD 환산 투자금    = 투자금(원) ÷ 환율
세전 연배당금(USD) = 투자금(USD) × 배당률(%) ÷ 100
세후 연배당금(USD) = 세전 연배당금 × (1 − 0.154)    // 배당소득세 15.4%
세전/세후 월배당금 = 연배당금 ÷ 12
원화 환산          = 각 USD 금액 × 환율
```

USD는 소수점 2자리, 원화는 정수(소수점 0자리)로 반올림하여 반환합니다.

### DRIP 복리 시뮬레이션 (`DripCalculatorServiceImpl`)

```
월 세후 수익률 = (연배당률(%) ÷ 100 ÷ 12) × (1 − 0.154)

매월 반복:
  [적립식] 자산 += 월 추가 납입금(USD)
  월 배당금     = 자산 × 월 세후 수익률
  자산         += 월 배당금          // 배당 즉시 재투자 (DRIP)
  누적배당금   += 월 배당금

수익률(%) = 누적배당금 ÷ 실투자금 × 100
```

KRW → USD 환산(`÷ 환율`)은 `SimulationController`에서 처리하며, 원화 실투자금은 환산 오차 방지를 위해 원화 기준으로 직접 재설정합니다.

### 포트폴리오 배당 합산 (`PortfolioServiceImpl`)

```
// portfolio LEFT JOIN etf_info 단일 쿼리로 N+1 문제 해결
투자금(USD)   = 투자금(원) ÷ 환율
세후 월배당(USD) = 투자금(USD) × divYield(%) ÷ 100 × (1 − 0.154) ÷ 12
세후 월배당(원)  = 세후 월배당(USD) × 환율
세후 연배당(원)  = 세후 월배당(원) × 12
// 전체 포트폴리오 합계를 루프에서 누산하여 반환
```

### 세후 배당이율 (`EtfController`)

```
세후 배당이율(%) = divYield × 0.846    // 화면 표시용, scale(2, HALF_UP)
```

---

## 데이터 수집 초기 설정 (step 순서)

```bash
# 1. 환경변수 설정
cp .env.example .env
# .env에 POLYGON_API_KEY, KOREAEXIM_API_KEY, DB_* 설정

# 2. 패키지 설치
cd fastapi
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# 3. 초기 데이터 적재 (순서 중요)
python scripts/step0_load_exchange_rate.py   # 환율 적재
python scripts/step1_load_etf_master.py      # ETF 100개 + 가격 적재 (약 25분 소요, rate limit 대기)
python scripts/step2_load_dividend.py        # 배당 내역 12개월 적재 (약 22분 소요)
python scripts/step3_load_descriptions.py    # ETF 한국어 설명 적재 (CSV 필요)

# 4. 적재 검증
python tests/verify_top100.py
```

> `step1`, `step2`는 Polygon.io 무료 요금제(5 req/min) 준수를 위해 각 티커 처리 후 `time.sleep(12~13)`을 적용합니다.

---

## 자동화 스케줄 (crontab, KST 기준)

```bash
# 타임존 KST 설정 (최초 1회)
sudo timedatectl set-timezone Asia/Seoul

# 환율 업데이트 (평일 오전 11:05 KST — 수출입은행 11시 공시 후 5분 여유)
5 11 * * 1-5 /home/ubuntu/etf-simulator/fastapi/.venv/bin/python \
  /home/ubuntu/etf-simulator/fastapi/scripts/step5_update_exchange.py \
  >> /home/ubuntu/etf-simulator/fastapi/logs/cron_exchange.log 2>&1

# ETF 가격·배당 업데이트 (월~토 오전 08:00 KST — 미국 장 마감 후 2시간 여유)
0 8 * * 1-6 /home/ubuntu/etf-simulator/fastapi/.venv/bin/python \
  /home/ubuntu/etf-simulator/fastapi/scripts/step4_update_prices.py \
  >> /home/ubuntu/etf-simulator/fastapi/logs/cron_prices.log 2>&1
```

| 스케줄 | 시간 (KST) | 대상 | 이유 |
|---|---|---|---|
| 환율 | 평일 11:05 | 한국수출입은행 | 매 영업일 11시 공시 후 5분 여유 |
| ETF 가격·배당 | 월~토 08:00 | Polygon.io | 미국 장 마감(한국 오전 6시) 후 2시간 여유 |

수집 결과는 `telegram_notify.py`를 통해 성공 종목 수·실패 종목 목록이 Telegram Bot으로 자동 전송됩니다.

---


