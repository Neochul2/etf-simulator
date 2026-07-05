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
        </ul>
    </div>
</nav>

<div class="container">

    <span class="badge badge-screen mb-2">📊 ETF INFO</span>
    <h2 class="fw-bold">미국 월배당 ETF</h2>
    <p class="text-muted">시총 상위 월배당 ETF 100개 중 하나를 검색하고 배당 정보를 확인하세요.</p>

    <div class="row g-2 mb-4 align-items-center">
        <div class="col-md-4">
            <input type="text" id="searchInput" class="form-control"
                placeholder="ETF명 또는 티커를 입력하세요 (예: QQQI, JEPI)">
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
            <small class="text-muted" id="updatedAt">데이터 기준 시간 조회 전</small>
            <br>
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
                <h6 class="mb-3">최근 6개월 배당 지급 내역</h6>
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
var exchangeRateValue = 0;

window.addEventListener('DOMContentLoaded', function() {
    loadExchangeRate();
    loadSymbolList();
});      
       
function loadExchangeRate() {
    fetch(contextPath + '/exchange/latest.do')
        .then(function(res) { return res.json(); })
        .then(function(rate) {
            exchangeRateValue = Number(rate.rate);
        });
}

function loadSymbolList() {
    return fetch(contextPath + '/etf/symbols.do')
        .then(function(res) { return res.json(); })
        .then(function(list) {
            var select = document.getElementById('symbolSelect');
            list.forEach(function(item) {
                var opt = document.createElement('option');
                opt.value = item.symbol;
                opt.text = item.symbol + ' (배당률 ' + item.divYield + '%)';
                select.appendChild(opt);
            });

            // ← 드롭다운 다 채운 후 여기서 복원
            var lastSymbol = sessionStorage.getItem('lastSymbol');
            if (lastSymbol) {
                document.getElementById('searchInput').value = lastSymbol;
                document.getElementById('symbolSelect').value = lastSymbol;
                searchEtf(lastSymbol);
            }
        });
}

document.getElementById('symbolSelect').addEventListener('change', function() {
    if (this.value) {
        document.getElementById('searchInput').value = this.value;
        searchEtf(this.value);
    }
});

document.getElementById('searchBtn').addEventListener('click', function() {
    runSearch();
});

document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') { runSearch(); }
});

function runSearch() {
    var symbol = document.getElementById('searchInput').value.trim().toUpperCase();
    if (symbol) { searchEtf(symbol); }
}

function searchEtf(symbol) {
    sessionStorage.setItem('lastSymbol', symbol);
    
    // 로딩 표시
    document.getElementById('resultArea').style.display = 'block';
    document.getElementById('logoBox').innerText = '...';
    document.getElementById('etfName').innerText = '조회 중...';
    document.getElementById('price').innerText = '-';
    document.getElementById('divYield').innerText = '-';

    fetch(contextPath + '/etf/' + symbol + '/detail.do')
        .then(function(res) {
            if (!res.ok) throw new Error('조회 실패');
            return res.json();
        })
        .then(function(data) {
            currentSymbol = symbol;
            renderEtf(data);
        })
        .catch(function() {
            alert('해당 티커를 찾을 수 없습니다: ' + symbol);
        });
}

function renderEtf(data) {
    var info = data.info;
    var dividends = data.dividends;

    document.getElementById('resultArea').style.display = 'block';
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
        var krwAmount = Math.round(d.cashAmount * exchangeRateValue);
        var row = '<tr>'
            + '<td>' + d.payDate + '</td>'
            + '<td>' + d.exDivDate + '</td>'
            + '<td>$' + d.cashAmount + '</td>'
            + '<td>≈ ' + krwAmount.toLocaleString() + '원</td>'
            + '</tr>';
        tbody.innerHTML += row;
    });
}
</script>

</body>
</html>