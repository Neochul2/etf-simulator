<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>미국 월배당 ETF - 내 포트폴리오</title>
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<style>
body { background-color: #f8f9fa; }
.navbar-brand { font-weight: bold; }
.nav-link.active { border-bottom: 3px solid #0d6efd; font-weight: bold; }
.badge-screen { background-color: #0d6efd; }
.summary-card { background: #fff; border-radius: 12px; padding: 20px;
    text-align: center; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.summary-card .label { font-size: 0.78rem; color: #6c757d; margin-bottom: 4px; }
.summary-card .value { font-size: 1.3rem; font-weight: 700; }
.amt-edit-btn { font-size: 0.75rem; text-decoration: none; }
.amt-input { max-width: 160px; display: inline-block; }
</style>
</head>
<body>

<nav class="navbar navbar-expand bg-white border-bottom mb-4">
    <div class="container">
        <a class="navbar-brand" href="<%=request.getContextPath()%>/etf/list.do">📊 미국 월배당 ETF</a>
        <ul class="navbar-nav mx-auto">
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/list.do">ETF 조회</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/calculator.do">배당금 계산기</a></li>
            <li class="nav-item"><a class="nav-link" href="<%=request.getContextPath()%>/etf/simulator.do">재투자 시뮬레이션</a></li>
            <li class="nav-item"><a class="nav-link active" href="<%=request.getContextPath()%>/etf/portfolio.do">내 포트폴리오</a></li>
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

    <span class="badge badge-screen mb-2">💼 포트폴리오</span>
    <h2 class="fw-bold">내 ETF 포트폴리오</h2>
    <p class="text-muted">보유 ETF와 투자금액을 입력하면 예상 월배당금을 자동으로 계산합니다.</p>

    <%-- 종목 추가 --%>
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <h6 class="fw-bold mb-3">➕ 종목 추가</h6>
            <div class="row g-2 align-items-end">
                <div class="col-md-4">
                    <label class="form-label text-muted" style="font-size:0.82rem;">ETF 종목 선택</label>
                    <select class="form-select" id="addSymbol">
                        <option value="">종목 선택</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label text-muted" style="font-size:0.82rem;">투자금액 (원화)</label>
                    <input type="text" class="form-control" id="addInvestAmt" placeholder="예: 5,000,000">
                </div>
                <div class="col-md-2">
                    <button class="btn btn-primary w-100" onclick="addPortfolio()">추가</button>
                </div>
            </div>
            <small class="text-muted mt-2 d-block">
                현재 환율: <span id="exchangeRateDisp">-</span> KRW/USD
            </small>
        </div>
    </div>

    <%-- 요약 카드 --%>
    <div class="row g-3 mb-4">
        <div class="col-md-3">
            <div class="summary-card">
                <div class="label">총 투자금액</div>
                <div class="value text-dark">
                    <fmt:formatNumber value="${totalAmt}" pattern="#,##0"/>원
                </div>
                <div class="text-muted" style="font-size:0.82rem;">
                    $<fmt:formatNumber value="${totalAmtUsd}" pattern="#,##0.00"/>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="summary-card">
                <div class="label">세후 월배당금 합계</div>
                <div class="value text-success">
                    <fmt:formatNumber value="${totalMonthlyDiv}" pattern="#,##0"/>원
                </div>
                <div class="text-muted" style="font-size:0.82rem;">
                    $<fmt:formatNumber value="${totalMonthlyDivUsd}" pattern="#,##0.00"/>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="summary-card">
                <div class="label">세후 연배당금 합계</div>
                <div class="value text-success">
                    <fmt:formatNumber value="${totalMonthlyDiv * 12}" pattern="#,##0"/>원
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="summary-card">
                <div class="label">보유 종목 수</div>
                <div class="value text-primary">${portfolioList.size()}개</div>
            </div>
        </div>
    </div>

    <%-- 포트폴리오 테이블 --%>
    <div class="card shadow-sm">
        <div class="card-body">
            <h6 class="fw-bold mb-3">📋 보유 종목 목록</h6>
            <c:choose>
                <c:when test="${empty portfolioList}">
                    <p class="text-muted text-center py-4">아직 추가된 종목이 없습니다. 위에서 종목을 추가해주세요.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>종목</th>
                                    <th>ETF명</th>
                                    <th>배당률</th>
                                    <th>투자금액</th>
                                    <th>세후 월배당금</th>
                                    <th>세후 연배당금</th>
                                    <th>삭제</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${portfolioList}">
                                <tr>
                                    <td><span class="badge bg-primary">${p.symbol}</span></td>
                                    <td style="font-size:0.85rem; color:#6c757d;">${p.issuer}</td>
                                    <td class="text-success fw-bold">
                                        <fmt:formatNumber value="${p.divYield}" pattern="#,##0.00"/>%
                                    </td>
                                    <td class="invest-amt-cell" data-id="${p.id}">
                                        <span class="amt-display">
                                            <fmt:formatNumber value="${p.investAmt}" pattern="#,##0"/>원<br>
                                            <small class="text-muted">$<fmt:formatNumber value="${p.investUsd}" pattern="#,##0.00"/></small>
                                            <br>
                                            <a href="javascript:void(0)" class="amt-edit-btn">✏️ 수정</a>
                                        </span>
                                        <div class="amt-edit" style="display:none;">
                                            <input type="text" class="form-control form-control-sm amt-input"
                                                   value="<fmt:formatNumber value="${p.investAmt}" pattern="#,##0"/>">
                                            <div class="mt-1">
                                                <button class="btn btn-sm btn-primary amt-save-btn">저장</button>
                                                <button class="btn btn-sm btn-outline-secondary amt-cancel-btn">취소</button>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="text-success fw-bold">
                                        <fmt:formatNumber value="${p.monthlyDiv}" pattern="#,##0"/>원<br>
                                        <small class="text-muted">$<fmt:formatNumber value="${p.monthlyDivUsd}" pattern="#,##0.00"/></small>
                                    </td>
                                    <td class="text-success">
                                        <fmt:formatNumber value="${p.monthlyDiv * 12}" pattern="#,##0"/>원
                                    </td>
                                    <td>
                                        <button class="btn btn-sm btn-outline-danger"
                                                onclick="deletePortfolio(${p.id})">삭제</button>
                                    </td>
                                </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <p class="text-muted mt-3" style="font-size:0.8rem;">
        ※ 배당률은 최근 12개월 기준이며, 배당소득세 15.4% 차감 후 세후 금액 기준입니다.<br>
        ※ 실제 수령액은 주가·환율·세제 변동에 따라 달라질 수 있습니다.
    </p>

</div>

<script src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
var CTX = '<%=request.getContextPath()%>';

// 숫자 콤마 포맷
function formatNumber(val) {
    var num = val.toString().replace(/,/g, '').replace(/[^0-9]/g, '');
    return num ? Number(num).toLocaleString() : '';
}
function parseNumber(val) {
    return parseFloat(val.toString().replace(/,/g, '')) || 0;
}

// 네비바 환율 표시
fetch(CTX + '/exchange/latest.do')
    .then(function(r) { return r.json(); })
    .then(function(d) {
        document.getElementById('navExchangeRate').innerText =
            Number(d.rate).toLocaleString();
    });

// 환율 업데이트 버튼
function updateExchangeRate() {
    var btn = document.getElementById('navRateUpdateBtn');
    btn.disabled = true;
    btn.innerText = '⏳';
    fetch(CTX + '/exchange/update.do', { method: 'POST' })
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.status === 'ok') {
                document.getElementById('navExchangeRate').innerText =
                    Number(d.rate).toLocaleString();
                btn.innerText = '✅';
            } else {
                btn.innerText = '❌';
            }
            setTimeout(function() {
                btn.disabled = false;
                btn.innerText = '🔄';
            }, 2000);
        });
}

window.onload = function() {
    // 환율 표시
    var rate = ${exchangeRate};
    document.getElementById('exchangeRateDisp').innerText = rate.toLocaleString();

    // 드롭다운 ETF 목록 로드
    fetch(CTX + '/etf/symbols.do')
        .then(function(r) { return r.json(); })
        .then(function(list) {
            var sel = document.getElementById('addSymbol');
            list.forEach(function(etf) {
                var opt = document.createElement('option');
                opt.value = etf.symbol;
                opt.text = etf.symbol + ' (' + etf.divYield + '%)';
                sel.appendChild(opt);
            });
        });

    // 투자금액 콤마 자동 추가
    document.getElementById('addInvestAmt').addEventListener('input', function() {
        this.value = formatNumber(this.value);
    });
};

// 종목 추가
function addPortfolio() {
    var symbol    = document.getElementById('addSymbol').value;
    var investAmt = parseNumber(document.getElementById('addInvestAmt').value);

    if (!symbol)      { alert('종목을 선택해주세요.'); return; }
    if (!investAmt)   { alert('투자금액을 입력해주세요.'); return; }

    var params = new URLSearchParams();
    params.append('symbol',    symbol);
    params.append('investAmt', investAmt);

    fetch(CTX + '/etf/portfolio/add.do', { method: 'POST', body: params })
        .then(function(r) { return r.text(); })
        .then(function(result) {
            if (result.includes('success')) {
                location.reload();
            } else {
                alert('추가 실패. 다시 시도해주세요.');
            }
        });
}

// 종목 삭제
function deletePortfolio(id) {
    if (!confirm('이 종목을 삭제하시겠습니까?')) return;

    var params = new URLSearchParams();
    params.append('id', id);

    fetch(CTX + '/etf/portfolio/delete.do', { method: 'POST', body: params })
        .then(function(r) { return r.text(); })
        .then(function(result) {
            if (result.includes('success')) {
                location.reload();
            } else {
                alert('삭제 실패. 다시 시도해주세요.');
            }
        });
}

// ===== 투자금액 인라인 수정 =====

// 콤마 자동 추가 (수정 입력창)
document.addEventListener('input', function(e) {
    if (e.target.classList.contains('amt-input')) {
        e.target.value = formatNumber(e.target.value);
    }
});

document.addEventListener('click', function(e) {

    // 수정 버튼 → 입력창 표시
    if (e.target.classList.contains('amt-edit-btn')) {
        var td = e.target.closest('.invest-amt-cell');
        td.querySelector('.amt-display').style.display = 'none';
        td.querySelector('.amt-edit').style.display = 'block';
    }

    // 취소 → 원래 표시로 복귀
    if (e.target.classList.contains('amt-cancel-btn')) {
        var td = e.target.closest('.invest-amt-cell');
        td.querySelector('.amt-display').style.display = '';
        td.querySelector('.amt-edit').style.display = 'none';
    }

    // 저장 → update.do 호출
    if (e.target.classList.contains('amt-save-btn')) {
        var td = e.target.closest('.invest-amt-cell');
        var id = td.dataset.id;
        var newAmt = parseNumber(td.querySelector('.amt-input').value);

        if (!newAmt || newAmt <= 0) {
            alert('올바른 금액을 입력해주세요.');
            return;
        }

        var params = new URLSearchParams();
        params.append('id', id);
        params.append('investAmt', newAmt);

        fetch(CTX + '/etf/portfolio/update.do', { method: 'POST', body: params })
            .then(function(r) { return r.text(); })
            .then(function(result) {
                if (result.includes('success')) {
                    location.reload();
                } else {
                    alert('수정 실패. 다시 시도해주세요.');
                }
            });
    }
});
</script>

</body>
</html>