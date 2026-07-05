<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>미국 월배당 ETF - 재투자 시뮬레이션</title>
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<style>
    body { background-color: #f8f9fa; }
    .navbar-brand { font-weight: bold; }
    .nav-link.active { border-bottom: 3px solid #0d6efd; font-weight: bold; }
    .badge-screen { background-color: #0d6efd; }
    .logo-box { width: 56px; height: 56px; background: #4338ca; color: #fff;
        display: flex; align-items: center; justify-content: center;
        font-weight: bold; border-radius: 10px; }
    .period-btn.active { border-color: #0d6efd !important; color: #0d6efd; font-weight: bold; }
</style>
</head>
<body>

<nav class="navbar navbar-expand bg-white border-bottom mb-4">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/etf/list.do">📊 미국 월배당 ETF</a>
        <ul class="navbar-nav mx-auto">
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/list.do">ETF 조회</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/calculator.do">배당금 계산기</a></li>
            <li class="nav-item"><a class="nav-link active" href="<%=request.getContextPath()%>/etf/simulator.do">재투자 시뮬레이션</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/portfolio.do">내 포트폴리오</a></li>
        </ul>
    </div>
</nav>

<div class="container">

    <span class="badge badge-screen mb-2">📈 시뮬레이션</span>
    <h2 class="fw-bold">배당금 재투자 시뮬레이션</h2>
    <p class="text-muted">선택한 ETF의 배당금을 재투자할 경우 예상 자산 변화를 시뮬레이션해보세요.</p>

    <div class="mb-3">
        <label class="form-label">종목 선택</label>
        <select class="form-select" id="symbolSelect" style="max-width: 300px;">
            <option value="">ETF를 선택하세요</option>
        </select>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-body d-flex align-items-center flex-wrap gap-4">
            <div class="d-flex align-items-center">
                <div class="logo-box me-3" id="logoBox">--</div>
                <div>
                    <h5 class="mb-1" id="etfName">ETF를 먼저 선택해주세요</h5>
                    <span class="badge bg-primary" id="symbolBadge"></span>
                </div>
            </div>
            <div class="text-center">
                <div class="text-muted small">현재가 (USD)</div>
                <div class="fw-bold" id="price">-</div>
            </div>
            <div class="text-center">
                <div class="text-muted small">배당률 (연)</div>
                <div class="fw-bold text-success" id="divYield">-</div>
            </div>
            <div class="text-center">
                <div class="text-muted small">세후 배당률 (연)</div>
                <div class="fw-bold text-success" id="afterTaxYield">-</div>
            </div>
            <div class="text-center">
                <div class="text-muted small">현재 환율 (KRW/USD)</div>
                <div class="fw-bold" id="exchangeRate">-</div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-md-6">
            <div class="card shadow-sm mb-3">
                <div class="card-body">
                    <h6 class="text-muted mb-3">1. 투자 조건 설정</h6>

                    <label class="form-label d-block">투자 방식</label>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="investType" id="lumpsum" value="lumpsum" checked>
                        <label class="form-check-label" for="lumpsum">목돈 일시납</label>
                    </div>
                    <div class="form-check form-check-inline mb-3">
                        <input class="form-check-input" type="radio" name="investType" id="monthly" value="monthly">
                        <label class="form-check-label" for="monthly">월 적립식</label>
                    </div>

                    <label class="form-label d-block mb-2">초기 투자금 (원)</label>
                    <input type="text" id="initialAmount" class="form-control mb-1" value="10,000,000">
                    <small class="text-muted d-block mb-3" id="usdConverted">≈ $0.00 USD</small>

                    <label class="form-label d-block mb-2">월 적립 금액 (원)</label>
                    <input type="text" id="monthlyAmount" class="form-control mb-3" value="500,000" disabled>

                    <label class="form-label d-block mb-2">투자 기간</label>
                    <div class="btn-group w-100 mb-4" role="group">
                        <button type="button" class="btn btn-outline-secondary period-btn" data-months="12">1년</button>
                        <button type="button" class="btn btn-outline-secondary period-btn" data-months="36">3년</button>
                        <button type="button" class="btn btn-outline-secondary period-btn active" data-months="60">5년</button>
                        <button type="button" class="btn btn-outline-secondary period-btn" data-months="120">10년</button>
                    </div>

                    <button class="btn btn-primary w-100 py-2" id="simulateBtn">🧮 시뮬레이션 돌리기</button>
                </div>
            </div>

            <div class="alert alert-light border small">
                ℹ️ 안내<br>
                · 배당금은 세후 금액을 기준으로 자동 재투자(DRIP) 됩니다.<br>
                · 주가 상승률, 배당률은 현재 기준으로 고정 가정됩니다.<br>
                · 월 적립식 선택 시 초기 투자금은 0원으로 고정됩니다.
            </div>
        </div>

        <div class="col-md-6">
            <h6 class="text-muted mb-3">2. 시뮬레이션 결과 (세후 기준)</h6>
            <div class="row g-3 mb-3">
                <div class="col-6">
                    <div class="card shadow-sm h-100">
                        <div class="card-body text-center">
                            <div class="text-muted small">실 투자금</div>
                            <div class="fs-4 fw-bold text-dark" id="totalInvestKrw">-</div>
                            <small class="text-muted" id="totalInvestUsd"></small>
                        </div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="card shadow-sm h-100">
                        <div class="card-body text-center">
                            <div class="text-muted small">세후 총 배당금</div>
                            <div class="fs-4 fw-bold text-success" id="totalDividendKrw">-</div>
                            <small class="text-muted" id="totalDividendUsd"></small>
                        </div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="card shadow-sm h-100">
                        <div class="card-body text-center">
                            <div class="text-muted small">총 자산 (기간 후)</div>
                            <div class="fs-4 fw-bold text-primary" id="finalAssetsKrw">-</div>
                            <small class="text-muted" id="finalAssetsUsd"></small>
                        </div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="card shadow-sm h-100">
                        <div class="card-body text-center">
                            <div class="text-muted small">수익률</div>
                            <div class="fs-4 fw-bold text-warning" id="returnRate">-</div>
                            <small class="text-muted" id="returnKrw"></small>
                        </div>
                    </div>
                </div>
            </div>

            <div class="alert alert-light border small mb-3">
                ℹ️ 안내<br>
                · 시뮬레이션 결과는 참고용이며, 실제 결과와 다를 수 있습니다.<br>
                · 주가 변동, 환율 변동, 세제 변경 등에 따라 달라질 수 있습니다.
            </div>

            <div class="d-flex gap-2">
                <button class="btn btn-outline-secondary flex-fill" id="resetBtn">🔄 다시 계산</button>
                <button class="btn btn-primary flex-fill" id="saveBtn">💾 결과 저장</button>
            </div>
        </div>
    </div>

</div>

<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
var contextPath = '<%=request.getContextPath()%>';
var currentSymbol = '';
var selectedMonths = 60;
var exchangeRateValue = 0;
var currentDivYield = 0;
var lastSimResult = null;

// 숫자 → 콤마 포맷
function formatNumber(val) {
    var num = val.toString().replace(/,/g, '').replace(/[^0-9]/g, '');
    return num ? Number(num).toLocaleString() : '';
}

// 콤마 제거 후 숫자 반환
function parseNumber(val) {
    return parseFloat(val.toString().replace(/,/g, '')) || 0;
}

window.addEventListener('DOMContentLoaded', function() {
    loadExchangeRate();
    loadSymbolList();
});

function loadExchangeRate() {
    fetch(contextPath + '/exchange/latest.do')
        .then(function(res) { return res.json(); })
        .then(function(rate) {
            exchangeRateValue = Number(rate.rate);
            document.getElementById('exchangeRate').innerText = exchangeRateValue.toLocaleString();
            updateUsdConverted();
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

document.getElementById('symbolSelect').addEventListener('change', function() {
    if (this.value) loadEtfInfo(this.value);
});

function loadEtfInfo(symbol) {
    fetch(contextPath + '/etf/' + symbol + '/detail.do')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            currentSymbol = symbol;
            var info = data.info;

            document.getElementById('logoBox').innerText = symbol;
            document.getElementById('etfName').innerText = info.issuer || symbol;
            document.getElementById('symbolBadge').innerText = symbol;
            document.getElementById('price').innerText = '$' + info.price;
            document.getElementById('divYield').innerText = info.divYield + '%';
            document.getElementById('afterTaxYield').innerText =
                (info.divYield * (1 - 0.154)).toFixed(2) + '%';

            currentDivYield = info.divYield;
            updateUsdConverted();
        })
        .catch(function() {
            alert('해당 티커를 찾을 수 없습니다: ' + symbol);
        });
}

// 투자금액 콤마 자동 추가
document.getElementById('initialAmount').addEventListener('input', function() {
    this.value = formatNumber(this.value);
    updateUsdConverted();
});

document.getElementById('monthlyAmount').addEventListener('input', function() {
    this.value = formatNumber(this.value);
});

document.querySelectorAll('input[name="investType"]').forEach(function(radio) {
    radio.addEventListener('change', function() {
        var isMonthly = document.getElementById('monthly').checked;
        document.getElementById('monthlyAmount').disabled = !isMonthly;

        var initialAmountInput = document.getElementById('initialAmount');
        if (isMonthly) {
            initialAmountInput.value = '0';
            initialAmountInput.disabled = true;
        } else {
            initialAmountInput.disabled = false;
        }
        updateUsdConverted();
    });
});

document.querySelectorAll('.period-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.period-btn').forEach(function(b) {
            b.classList.remove('active');
        });
        this.classList.add('active');
        selectedMonths = parseInt(this.getAttribute('data-months'));
    });
});

function updateUsdConverted() {
    var krw = parseNumber(document.getElementById('initialAmount').value);
    if (exchangeRateValue > 0) {
        document.getElementById('usdConverted').innerText =
            '≈ $' + (krw / exchangeRateValue).toFixed(2) + ' USD';
    }
}

document.getElementById('simulateBtn').addEventListener('click', simulate);

function simulate() {
    if (!currentSymbol) { alert('종목을 먼저 선택해주세요.'); return; }

    var initialAmountKrw = parseNumber(document.getElementById('initialAmount').value);
    var monthlyAmountKrw = parseNumber(document.getElementById('monthlyAmount').value);
    var isMonthlyInvest = document.getElementById('monthly').checked;

    var initialAmountUsd = (initialAmountKrw / exchangeRateValue).toFixed(2);
    var monthlyAmountUsd = isMonthlyInvest
        ? (monthlyAmountKrw / exchangeRateValue).toFixed(2) : 0;

    var url = contextPath + '/etf/' + currentSymbol + '/simulate.do'
        + '?initialAmount=' + initialAmountUsd
        + '&monthlyAmount=' + monthlyAmountUsd
        + '&months=' + selectedMonths
        + '&isMonthlyInvest=' + isMonthlyInvest;

    fetch(url)
        .then(function(res) { return res.json(); })
        .then(function(result) { renderResult(result); });
}

function renderResult(r) {
    var totalInvestKrw   = Math.round(r.totalInvest   * exchangeRateValue);
    var totalDividendKrw = Math.round(r.totalDividend * exchangeRateValue);
    var finalAssetsKrw   = Math.round(r.finalAssets   * exchangeRateValue);

    document.getElementById('totalInvestKrw').innerText = totalInvestKrw.toLocaleString() + ' 원';
    document.getElementById('totalInvestUsd').innerText = '(약 $' + Number(r.totalInvest).toFixed(2) + ')';
    document.getElementById('totalDividendKrw').innerText = totalDividendKrw.toLocaleString() + ' 원';
    document.getElementById('totalDividendUsd').innerText = '(약 $' + Number(r.totalDividend).toFixed(2) + ')';
    document.getElementById('finalAssetsKrw').innerText = finalAssetsKrw.toLocaleString() + ' 원';
    document.getElementById('finalAssetsUsd').innerText = '(약 $' + Number(r.finalAssets).toFixed(2) + ')';
    document.getElementById('returnRate').innerText = r.returnRate + '%';
    document.getElementById('returnKrw').innerText = '순수익 ' + totalDividendKrw.toLocaleString() + '원';

    lastSimResult = r;
}

document.getElementById('resetBtn').addEventListener('click', function() {
    document.getElementById('initialAmount').value = '10,000,000';
    document.getElementById('initialAmount').disabled = false;
    document.getElementById('monthlyAmount').value = '500,000';
    document.getElementById('lumpsum').checked = true;
    document.getElementById('monthlyAmount').disabled = true;
    selectedMonths = 60;
    document.querySelectorAll('.period-btn').forEach(function(b) { b.classList.remove('active'); });
    document.querySelector('.period-btn[data-months="60"]').classList.add('active');
    updateUsdConverted();

    document.getElementById('totalInvestKrw').innerText  = '-';
    document.getElementById('totalInvestUsd').innerText  = '';
    document.getElementById('totalDividendKrw').innerText = '-';
    document.getElementById('totalDividendUsd').innerText = '';
    document.getElementById('finalAssetsKrw').innerText  = '-';
    document.getElementById('finalAssetsUsd').innerText  = '';
    document.getElementById('returnRate').innerText      = '-';
    document.getElementById('returnKrw').innerText       = '';

    lastSimResult = null;
});

document.getElementById('saveBtn').addEventListener('click', function() {
    if (!currentSymbol) { alert('종목을 먼저 선택해주세요.'); return; }
    if (!lastSimResult) { alert('시뮬레이션을 먼저 실행해주세요.'); return; }

    var isMonthlyInvest  = document.getElementById('monthly').checked;
    var initialAmountKrw = parseNumber(document.getElementById('initialAmount').value);
    var monthlyAmountKrw = parseNumber(document.getElementById('monthlyAmount').value);
    var initialAmtUsd    = (initialAmountKrw / exchangeRateValue).toFixed(2);
    var monthlyAmtUsd    = isMonthlyInvest
        ? (monthlyAmountKrw / exchangeRateValue).toFixed(2) : 0;

    var params = new URLSearchParams();
    params.append('investType',  isMonthlyInvest ? 'monthly' : 'lumpsum');
    params.append('initialAmt',  initialAmtUsd);
    params.append('monthlyAmt',  monthlyAmtUsd);
    params.append('months',      selectedMonths);
    params.append('divYield',    currentDivYield);
    params.append('totalDiv',    lastSimResult.totalDividend);
    params.append('finalAssets', lastSimResult.finalAssets);

    fetch(contextPath + '/etf/' + currentSymbol + '/simulation/save.do', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(function(res) { return res.json(); })
    .then(function(result) {
        if (result.status === 'ok') {
            alert('저장 완료! (저장 ID: ' + result.savedId + ')');
        } else {
            alert('저장 실패. 다시 시도해주세요.');
        }
    })
    .catch(function() {
        alert('저장 중 오류가 발생했습니다.');
    });
});
</script>

</body>
</html>