<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>월배당 ETF - 배당금 계산기</title>
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<style>
body { background-color: #f8f9fa; }
.navbar-brand { font-weight: bold; }
.nav-link.active { border-bottom: 3px solid #0d6efd; font-weight: bold; }
.badge-screen { background-color: #0d6efd; }
.logo-box { width: 56px; height: 56px; background: #4338ca; color: #fff;
    display: flex; align-items: center; justify-content: center;
    font-weight: bold; border-radius: 10px; }
.result-card { background: #fff; border-radius: 8px; padding: 14px; text-align: center; }
</style>
</head>
<body>

<nav class="navbar navbar-expand bg-white border-bottom mb-4">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/etf/list.do">📊 미국 월배당 ETF</a>
        <ul class="navbar-nav mx-auto">
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/list.do">ETF 조회</a></li>
            <li class="nav-item"><a class="nav-link active" href="<%=request.getContextPath()%>/etf/calculator.do">배당금 계산기</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/simulator.do">재투자 시뮬레이션</a></li>
        </ul>
    </div>
</nav>

<div class="container">

    <span class="badge badge-screen mb-2">화면 2</span>
    <h2 class="fw-bold">월배당금 계산기</h2>
    <p class="text-muted">선택한 ETF의 월배당금 및 연배당금을 계산해보세요.</p>

    <%-- 종목 검색 + 드롭다운 (화면1·3과 동일 패턴, URL 파라미터 연동 없음) --%>
    <div class="row g-2 mb-4">
        <div class="col-md-4">
            <input type="text" id="searchInput" class="form-control" placeholder="티커 입력 (예: JEPI)">
        </div>
        <div class="col-auto">
            <button class="btn btn-primary px-4" id="searchBtn">검색</button>
        </div>
        <div class="col-md-4">
            <select class="form-select" id="symbolSelect">
                <option value="">시총 상위 ETF 목록</option>
            </select>
        </div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-body d-flex align-items-center">
            <div class="logo-box me-3" id="logoBox">--</div>
            <div>
                <h5 class="mb-1" id="etfName">ETF를 먼저 선택해주세요</h5>
                <span class="badge bg-primary" id="symbolBadge"></span>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-md-6">

            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="text-muted mb-2">1. 현재 환율 (1일 1회 업데이트)</h6>
                    <div class="fs-3 fw-bold" id="exchangeRate">조회 중...</div>
                </div>
            </div>

            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="text-muted mb-2">2. 투자 정보 입력</h6>
                    <label class="form-label">투자금액 (원)</label>
                    <input type="number" id="investAmount" class="form-control" value="10000000">
                    <small class="text-muted" id="usdConverted">≈ $0.00 USD</small>
                    <button class="btn btn-primary w-100 py-2 mt-3" id="calculateBtn">🧮 계산하기</button>
                </div>
            </div>

            <div class="alert alert-light border small">
                ℹ️ 안내<br>
                · 환율은 1일 1회 업데이트됩니다.<br>
                · 투자금액은 원화 기준으로 입력해주세요.
            </div>
        </div>

        <div class="col-md-6">

            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="text-muted mb-3">3. 배당률 및 세금 정보</h6>
                    <div class="row text-center">
                        <div class="col-4">
                            <div class="text-muted small">배당률 (연)</div>
                            <div class="fs-5 fw-bold text-success" id="divYield">-</div>
                        </div>
                        <div class="col-4">
                            <div class="text-muted small">세금</div>
                            <div class="fs-5 fw-bold">15.4%</div>
                        </div>
                        <div class="col-4">
                            <div class="text-muted small">세후 배당률 (연)</div>
                            <div class="fs-5 fw-bold text-success" id="afterTaxYield">-</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="text-muted mb-3">4. 계산 결과 (연 기준)</h6>
                    <div class="row g-2 text-center">
                        <div class="col-6">
                            <div class="result-card">
                                <div class="text-muted small">세전 월 배당금</div>
                                <div class="fs-5 fw-bold" id="beforeTaxMonthly">-</div>
                                <small class="text-muted" id="beforeTaxMonthlyUsd"></small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="result-card">
                                <div class="text-muted small">세후 월 배당금</div>
                                <div class="fs-5 fw-bold" id="afterTaxMonthly">-</div>
                                <small class="text-muted" id="afterTaxMonthlyUsd"></small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="result-card">
                                <div class="text-muted small">세전 연 배당금</div>
                                <div class="fs-5 fw-bold text-success" id="beforeTaxYearly">-</div>
                                <small class="text-muted" id="beforeTaxYearlyUsd"></small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="result-card">
                                <div class="text-muted small">세후 연 배당금</div>
                                <div class="fs-5 fw-bold text-success" id="afterTaxYearly">-</div>
                                <small class="text-muted" id="afterTaxYearlyUsd"></small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <div class="alert alert-light border small my-4">
        ℹ️ 안내<br>
        · 배당금은 최근 12개월 지급 내역 기준으로 산출되며, 배당률 및 환율 변동에 따라 달라질 수 있습니다.<br>
        · 실제 투자 결과와 다를 수 있습니다.
    </div>

</div>

<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
var contextPath = '<%=request.getContextPath()%>';
var currentSymbol = '';
var currentDivYield = 0;
var exchangeRateValue = 0;

window.addEventListener('DOMContentLoaded', function() {
    loadExchangeRate();
    loadSymbolList();
    // URL 파라미터 연동 완전 제거 — 독립 운영
});

function loadExchangeRate() {
    fetch(contextPath + '/exchange/latest.do')
        .then(function(res) { return res.json(); })
        .then(function(rate) {
            exchangeRateValue = Number(rate.rate);
            document.getElementById('exchangeRate').innerText =
                exchangeRateValue.toLocaleString() + ' KRW / USD';
        });
}

function loadSymbolList() {
    fetch(contextPath + '/etf/symbols.do')
        .then(function(res) { return res.json(); })
        .then(function(list) {
            var select = document.getElementById('symbolSelect');
            list.forEach(function(item) {
                var opt = document.createElement('option');
                opt.value = item.symbol;
                opt.text = item.symbol + ' (배당률 ' + item.divYield + '%)';
                select.appendChild(opt);
            });
        });
}

// 드롭다운 선택 시 종목 로드 + 검색창 동기화
document.getElementById('symbolSelect').addEventListener('change', function() {
    if (this.value) {
        document.getElementById('searchInput').value = this.value;
        loadEtfInfo(this.value);
    }
});

// 검색 버튼 클릭
document.getElementById('searchBtn').addEventListener('click', function() {
    var symbol = document.getElementById('searchInput').value.trim().toUpperCase();
    if (!symbol) {
        alert('티커를 입력해주세요.');
        return;
    }
    loadEtfInfo(symbol);
});

// 엔터키 검색
document.getElementById('searchInput').addEventListener('keydown', function(e) {
    if (e.key === 'Enter') {
        document.getElementById('searchBtn').click();
    }
});

function loadEtfInfo(symbol) {
    fetch(contextPath + '/etf/' + symbol + '/detail.do')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            currentSymbol = symbol;
            currentDivYield = data.info.divYield;

            document.getElementById('logoBox').innerText = symbol;
            document.getElementById('etfName').innerText = symbol;
            document.getElementById('symbolBadge').innerText = symbol;
            document.getElementById('divYield').innerText = currentDivYield + '%';
            document.getElementById('afterTaxYield').innerText =
                (currentDivYield * (1 - 0.154)).toFixed(2) + '%';

            updateUsdConverted();
        })
        .catch(function() {
            alert('해당 티커를 찾을 수 없습니다: ' + symbol);
        });
}

document.getElementById('investAmount').addEventListener('input', updateUsdConverted);

function updateUsdConverted() {
    var krw = parseFloat(document.getElementById('investAmount').value) || 0;
    if (exchangeRateValue > 0) {
        document.getElementById('usdConverted').innerText =
            '≈ $' + (krw / exchangeRateValue).toFixed(2) + ' USD';
    }
}

document.getElementById('calculateBtn').addEventListener('click', calculate);

function calculate() {
    if (!currentSymbol) {
        alert('종목을 먼저 선택해주세요.');
        return;
    }
    var investAmount = document.getElementById('investAmount').value;
    fetch(contextPath + '/etf/' + currentSymbol + '/calculate.do?investAmount=' + investAmount)
        .then(function(res) { return res.json(); })
        .then(function(result) {
            renderResult(result);
        });
}

function renderResult(r) {
    document.getElementById('usdConverted').innerText = '≈ $' + r.investAmountUsd.toFixed(2) + ' USD';

    document.getElementById('beforeTaxMonthly').innerText = Math.round(r.beforeTaxMonthlyKrw).toLocaleString() + ' 원';
    document.getElementById('beforeTaxMonthlyUsd').innerText = '($' + r.beforeTaxMonthlyUsd.toFixed(2) + ')';

    document.getElementById('afterTaxMonthly').innerText = Math.round(r.afterTaxMonthlyKrw).toLocaleString() + ' 원';
    document.getElementById('afterTaxMonthlyUsd').innerText = '($' + r.afterTaxMonthlyUsd.toFixed(2) + ')';

    document.getElementById('beforeTaxYearly').innerText = Math.round(r.beforeTaxYearlyKrw).toLocaleString() + ' 원';
    document.getElementById('beforeTaxYearlyUsd').innerText = '($' + r.beforeTaxYearlyUsd.toFixed(2) + ')';

    document.getElementById('afterTaxYearly').innerText = Math.round(r.afterTaxYearlyKrw).toLocaleString() + ' 원';
    document.getElementById('afterTaxYearlyUsd').innerText = '($' + r.afterTaxYearlyUsd.toFixed(2) + ')';
}
</script>

</body>
</html>