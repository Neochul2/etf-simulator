<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>미국 월배당 ETF - ETF 조회</title>
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<style>
body { background-color: #f8f9fa; }
.navbar-brand { font-weight: bold; }
.nav-link.active { border-bottom: 3px solid #0d6efd; font-weight: bold; }
.badge-screen { background-color: #0d6efd; }
.card-info { background: #f8f9fa; border-radius: 8px; padding: 12px; text-align: center; }
.logo-box { width: 64px; height: 64px; background: #4338ca; color: #fff;
    display: flex; align-items: center; justify-content: center;
    font-weight: bold; border-radius: 10px; }
.info-row { border-top: 1px solid #e9ecef; padding-top: 12px; margin-top: 12px; }
.info-item { text-align: center; }
.info-item .label { font-size: 0.75rem; color: #6c757d; margin-bottom: 2px; }
.info-item .value { font-size: 0.95rem; font-weight: 600; }
.desc-box { background: #f8f9fa; border-radius: 8px; border-left: 4px solid #0d6efd;
    padding: 12px 16px; margin-top: 12px; }
.autocomplete-box {
    position: absolute; z-index: 9999; background: #fff;
    border: 1px solid #dee2e6; border-radius: 6px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    max-height: 220px; overflow-y: auto; width: 100%;
}
.autocomplete-item { padding: 8px 12px; cursor: pointer; font-size: 0.9rem; }
.autocomplete-item:hover, .autocomplete-item.active { background-color: #e9f0ff; color: #0d6efd; }
.autocomplete-wrap { position: relative; }
</style>
</head>
<body>

<nav class="navbar navbar-expand bg-white border-bottom mb-4">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/etf/list.do">📊 미국 월배당 ETF</a>
        <ul class="navbar-nav mx-auto">
            <li class="nav-item"><a class="nav-link active" href="<%=request.getContextPath()%>/etf/list.do">ETF 조회</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/calculator.do">배당금 계산기</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/simulator.do">재투자 시뮬레이션</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/portfolio.do">내 포트폴리오</a></li>
        </ul>
        <div class="d-flex align-items-center ms-3">
            <span class="text-muted me-2" style="font-size:0.85rem;">
                💱 <span id="navExchangeRate">-</span> KRW
            </span>
            <button class="btn btn-sm btn-outline-secondary" id="navRateUpdateBtn"
                    onclick="updateExchangeRate()">🔄</button>
        </div>
    </div>
</nav>

<div class="container">

    <span class="badge badge-screen mb-2">📊 ETF INFO</span>
    <h2 class="fw-bold">미국 월배당 ETF</h2>
    <p class="text-muted">시총 상위 월배당 ETF 100개 중 하나를 검색하고 배당 정보를 확인하세요.</p>

    <div class="row g-2 mb-4 align-items-center">
        <div class="col-md-4 autocomplete-wrap">
            <input type="text" id="searchInput" class="form-control" autocomplete="off"
                placeholder="ETF명 또는 티커를 입력하세요 (예: QQQI, JEPI)">
            <div class="autocomplete-box" id="autocompleteBox" style="display:none;"></div>
        </div>
        <div class="col-md-2">
            <button class="btn btn-primary w-100" id="searchBtn">검색</button>
        </div>
        <div class="col-md-3">
            <select class="form-select" id="symbolSelect">
                <option value="">시총 상위 월배당 ETF (100)</option>
            </select>
        </div>
        <div class="col-md-3 text-end">
            <small class="text-muted" id="updatedAt">데이터 기준 시간 조회 전</small><br>
            <small class="text-muted">📌 출처: Polygon.io</small>
        </div>
    </div>

    <div id="resultArea" style="display: none;">
        <div class="card shadow-sm mb-4">
            <div class="card-body">
                <div class="d-flex align-items-center mb-4">
                    <div class="logo-box me-3" id="logoBox">--</div>
                    <div>
                        <h4 class="mb-1" id="etfName">-</h4>
                        <div class="mb-1" id="issuerName" style="color:#6c757d; font-size:0.9rem;"></div>
                        <span class="badge bg-primary" id="symbolBadge">-</span>
                        <span class="badge bg-light text-dark">미국</span>
                        <span class="badge bg-light text-dark">USD</span>
                    </div>
                </div>
                <div class="row text-center g-3 mb-3">
                    <div class="col-md-3">
                        <div class="card-info">
                            <div class="text-muted small">현재가 (USD)</div>
                            <div class="fs-4 fw-bold" id="price">-</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card-info">
                            <div class="text-muted small">배당률 (연)</div>
                            <div class="fs-4 fw-bold text-success" id="divYield">-</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card-info">
                            <div class="text-muted small">최근 배당락일</div>
                            <div class="fs-5 fw-bold text-primary" id="exDivDate">-</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card-info">
                            <div class="text-muted small">최근 지급일</div>
                            <div class="fs-5 fw-bold text-primary" id="payDate">-</div>
                        </div>
                    </div>
                </div>
                <div class="row text-center g-2 info-row">
                    <div class="col-3">
                        <div class="info-item">
                            <div class="label">시작가</div>
                            <div class="value" id="openPrice">-</div>
                        </div>
                    </div>
                    <div class="col-3">
                        <div class="info-item">
                            <div class="label">고가</div>
                            <div class="value text-danger" id="highPrice">-</div>
                        </div>
                    </div>
                    <div class="col-3">
                        <div class="info-item">
                            <div class="label">저가</div>
                            <div class="value text-primary" id="lowPrice">-</div>
                        </div>
                    </div>
                    <div class="col-3">
                        <div class="info-item">
                            <div class="label">거래량</div>
                            <div class="value" id="volume">-</div>
                        </div>
                    </div>
                </div>
                <div class="desc-box">
                    <small class="text-muted" id="etfDescription" style="line-height:1.6;"></small>
                    <small class="text-muted d-block mt-1" style="font-size:0.7rem;">
                        ※ AI 생성 설명입니다. 투자 참고용으로만 활용하세요.
                    </small>
                </div>
            </div>
        </div>

        <div class="card shadow-sm mb-4">
            <div class="card-body">
                <h6 class="mb-3">최근 12개월 배당 지급 내역</h6>
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>배당 지급일</th>
                            <th>배당락일</th>
                            <th>주당 배당금 (USD)</th>
                            <th>한화 금액 (원)</th>
                        </tr>
                    </thead>
                    <tbody id="dividendTableBody"></tbody>
                </table>
            </div>
        </div>
    </div>

</div>

<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
var contextPath = '<%=request.getContextPath()%>';
var currentSymbol = '';
var symbolList = [];

function updateExchangeRate() {
    var btn = document.getElementById('navRateUpdateBtn');
    btn.disabled = true;
    btn.innerText = '⏳';
    fetch(contextPath + '/exchange/update.do', { method: 'POST' })
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.status === 'ok') {
                document.getElementById('navExchangeRate').innerText = Number(d.rate).toLocaleString();
                btn.innerText = '✅';
            } else { btn.innerText = '❌'; }
            setTimeout(function() { btn.disabled = false; btn.innerText = '🔄'; }, 2000);
        });
}

window.addEventListener('DOMContentLoaded', function() {
    fetch(contextPath + '/exchange/latest.do')
        .then(function(res) { return res.json(); })
        .then(function(rate) {
            document.getElementById('navExchangeRate').innerText = Number(rate.rate).toLocaleString();
        });
    loadSymbolList();
});

function loadSymbolList() {
    fetch(contextPath + '/etf/symbols.do')
        .then(function(res) { return res.json(); })
        .then(function(list) {
            symbolList = list;
            var select = document.getElementById('symbolSelect');
            list.forEach(function(item) {
                var opt = document.createElement('option');
                opt.value = item.symbol;
                opt.text = item.symbol + ' (배당률 ' + item.divYield + '%)';
                select.appendChild(opt);
            });
            var lastSymbol = sessionStorage.getItem('lastSymbol');
            if (lastSymbol) {
                document.getElementById('searchInput').value = lastSymbol;
                document.getElementById('symbolSelect').value = lastSymbol;
                searchEtf(lastSymbol);
            }
        });
}

// 자동완성
document.getElementById('searchInput').addEventListener('input', function() {
    var val = this.value.trim().toUpperCase();
    var box = document.getElementById('autocompleteBox');
    document.getElementById('symbolSelect').value = val;
    if (!val) { box.style.display = 'none'; return; }
    var filtered = symbolList.filter(function(item) {
        return item.symbol.toUpperCase().indexOf(val) === 0;
    });
    if (filtered.length === 0) { box.style.display = 'none'; return; }
    box.innerHTML = '';
    filtered.slice(0, 8).forEach(function(item) {
        var div = document.createElement('div');
        div.className = 'autocomplete-item';
        div.innerHTML = '<strong>' + item.symbol + '</strong> <span class="text-muted" style="font-size:0.8rem;">' + item.divYield + '%</span>';
        div.addEventListener('click', function() {
            document.getElementById('searchInput').value = item.symbol;
            document.getElementById('symbolSelect').value = item.symbol;
            box.style.display = 'none';
            searchEtf(item.symbol);
        });
        box.appendChild(div);
    });
    box.style.display = 'block';
});

document.getElementById('searchInput').addEventListener('keydown', function(e) {
    var box = document.getElementById('autocompleteBox');
    var items = box.querySelectorAll('.autocomplete-item');
    var active = box.querySelector('.autocomplete-item.active');
    var idx = -1;
    items.forEach(function(item, i) { if (item === active) idx = i; });
    if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (idx < items.length - 1) { if (active) active.classList.remove('active'); items[idx+1].classList.add('active'); }
    } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (idx > 0) { if (active) active.classList.remove('active'); items[idx-1].classList.add('active'); }
    } else if (e.key === 'Enter') {
        if (active) { active.click(); }
        else { runSearch(); }
    } else if (e.key === 'Escape') {
        box.style.display = 'none';
    }
});

document.addEventListener('click', function(e) {
    var box = document.getElementById('autocompleteBox');
    var input = document.getElementById('searchInput');
    if (box && !box.contains(e.target) && e.target !== input) box.style.display = 'none';
});

document.getElementById('symbolSelect').addEventListener('change', function() {
    if (this.value) {
        document.getElementById('searchInput').value = this.value;
        document.getElementById('autocompleteBox').style.display = 'none';
        searchEtf(this.value);
    }
});

document.getElementById('searchBtn').addEventListener('click', function() { runSearch(); });

function runSearch() {
    var symbol = document.getElementById('searchInput').value.trim().toUpperCase();
    if (symbol) searchEtf(symbol);
}

function searchEtf(symbol) {
    sessionStorage.setItem('lastSymbol', symbol);
    document.getElementById('resultArea').style.display = 'block';
    document.getElementById('logoBox').innerText = '...';
    document.getElementById('etfName').innerText = '조회 중...';
    document.getElementById('price').innerText = '-';
    document.getElementById('divYield').innerText = '-';
    fetch(contextPath + '/etf/' + symbol + '/detail.do')
        .then(function(res) { if (!res.ok) throw new Error(); return res.json(); })
        .then(function(data) { currentSymbol = symbol; renderEtf(data); })
        .catch(function() {
            alert('해당 티커를 찾을 수 없습니다: ' + symbol);
            document.getElementById('searchInput').value = '';
            document.getElementById('symbolSelect').value = '';
            document.getElementById('resultArea').style.display = 'none';
            sessionStorage.removeItem('lastSymbol');
        });
}

function renderEtf(data) {
    var info = data.info;
    var dividends = data.dividends;
    document.getElementById('logoBox').innerText = info.symbol;
    document.getElementById('symbolBadge').innerText = info.symbol;
    document.getElementById('etfName').innerText = info.symbol;
    document.getElementById('updatedAt').innerText = 'Polygon.io 기준 · ' + info.updatedAt + ' 업데이트';
    document.getElementById('searchInput').value = info.symbol;
    document.getElementById('symbolSelect').value = info.symbol;
    document.getElementById('price').innerText = '$' + info.price;
    document.getElementById('divYield').innerText = info.divYield + '%';
    document.getElementById('issuerName').innerText = info.issuer || '';
    document.getElementById('openPrice').innerText = '$' + (info.openPrice || '-');
    document.getElementById('highPrice').innerText = '$' + (info.highPrice || '-');
    document.getElementById('lowPrice').innerText  = '$' + (info.lowPrice  || '-');
    document.getElementById('volume').innerText    = info.volume ? Number(info.volume).toLocaleString() : '-';
    document.getElementById('etfDescription').innerText = info.description || '';
    if (dividends.length > 0) {
        document.getElementById('exDivDate').innerText = dividends[0].exDivDate;
        document.getElementById('payDate').innerText   = dividends[0].payDate;
    }
    var tbody = document.getElementById('dividendTableBody');
    tbody.innerHTML = '';
    dividends.forEach(function(d) {
        tbody.innerHTML += '<tr><td>' + d.payDate + '</td><td>' + d.exDivDate + '</td><td>$' + d.cashAmount + '</td><td>≈ ' + Number(d.krwAmount).toLocaleString() + '원</td></tr>';
    });
}
</script>

</body>
</html>