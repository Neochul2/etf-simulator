# 🇺🇸 미국 월배당 ETF 시뮬레이터

> Polygon.io 실제 데이터 기반 배당 계산 · DRIP 복리 시뮬레이션 서비스

배포 URL: http://150.230.218.157:8080/etf-egov/etf/list.do

---

## 📌 프로젝트 개요

월배당 ETF 투자 수요 증가에 따라, 미국 시장의 실제 ETF 데이터를 활용한 배당 계산 및 재투자(DRIP) 복리 시뮬레이션 서비스를 구현하였습니다.

- **개발자**: 정대철
- **개발 기간**: 2026.06.22 ~ 2026.07.07
- **대상 ETF**: 시총 상위 100개 월배당 ETF (ETFdb.com 기준)

---

## 🖥️ 화면 구성

| 화면 | URL | 설명 |
|---|---|---|
| ETF 조회 | `/etf/list.do` | 종목 검색 → 현재가·배당률·6개월 배당내역 조회 |
| 배당금 계산기 | `/etf/calculator.do` | 투자금 입력 → 세전/세후 월·연 배당금 계산 |
| DRIP 시뮬레이션 | `/etf/simulator.do` | 목돈/적립식 선택 → 1·3·5·10년 복리 자산 예측 |
| 내 포트폴리오 | `/etf/portfolio.do` | 보유 ETF 등록·수정·삭제 → 배당금 합산 관리 |

---

## ⚙️ 기술 스택

| 구분 | 내용 |
|---|---|
| 언어 | Java 17 / Python 3.12.10 |
| 프레임워크 | eGovFrame 4.3.1 / FastAPI |
| 데이터베이스 | MySQL 8.0.46 |
| 배포 서버 | Apache Tomcat 9.0.86 / uvicorn + systemd |
| 인프라 | Oracle Cloud VM.Standard.E2.1.Micro (Ubuntu 24.04) |
| 외부 API | Polygon.io (주가·배당) / 한국수출입은행 (환율) |
| 자동화 | crontab + 텔레그램 Bot 알림 |
| 형상관리 | Git (feature → develop → main) |

---

## 🗄️ DB 구성

| 테이블 | 설명 |
|---|---|
| `etf_info` | ETF 기본정보 (현재가·배당률·OHLCV·설명) |
| `etf_dividend` | 배당 내역 (배당락일·지급일·주당배당금) |
| `exchange_rate` | USD/KRW 환율 |
| `simulation_result` | DRIP 시뮬레이션 결과 저장 |
| `portfolio` | 사용자 보유 ETF 및 투자금액 |

---

## 🔄 아키텍처

```
[Polygon.io API]       [수출입은행 API]
       ↓                      ↓
   [FastAPI] ────────→ [MySQL 8.0.46]
                              ↓
                      [eGovFrame (Tomcat)]
                              ↓
                        [JSP 화면]
```

- **FastAPI**: 데이터 수집·적재 (crontab 매일 자동 실행)
- **eGovFrame**: DB에서 읽어 화면 서비스 제공

---

## 🚀 배포 환경

```
서버: Oracle Cloud VM (Ubuntu 24.04)
IP: 150.230.218.157

서비스 자동 기동 (systemd):
  - mysql.service
  - fastapi.service
  - tomcat.service

데이터 자동 수집 (crontab, KST 기준):
  - 11:30 → 환율 업데이트
  - 11:40 → ETF 가격·배당 업데이트
  - 결과 → 텔레그램 Bot 알림
```

---

## 💡 핵심 구현 포인트

- **DRIP 복리 계산**: Java BigDecimal로 세후 15.4% 재투자 복리 계산 (목돈/적립식)
- **환율 자동 적용**: 수출입은행 API 1일 1회 갱신, 모든 계산에 자동 반영
- **N+1 쿼리 개선**: 포트폴리오 목록 LEFT JOIN 1회 쿼리로 최적화
- **2-tier 아키텍처**: FastAPI(수집·적재) + eGovFrame(화면 서비스) 역할 분리
