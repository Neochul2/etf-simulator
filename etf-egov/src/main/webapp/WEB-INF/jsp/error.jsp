<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>미국 월배당 ETF - 오류 안내</title>
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<style>
    body { background-color: #f8f9fa; height: 100vh; display: flex; align-items: center; justify-content: center; }
    .error-box { text-align: center; max-width: 500px; }
    .error-code { font-size: 4rem; font-weight: bold; color: #0d6efd; }
</style>
</head>
<body>

<div class="error-box">
    <div class="error-code">⚠️</div>
    <h3 class="fw-bold mb-3">요청하신 페이지를 처리할 수 없습니다</h3>
    <p class="text-muted mb-4">
        페이지를 찾을 수 없거나 일시적인 오류가 발생했습니다.<br>
        잠시 후 다시 시도해주세요.
    </p>
    <a href="<%=request.getContextPath()%>/etf/list.do" class="btn btn-primary px-4">
        ETF 조회 화면으로 이동
    </a>
</div>

</body>
</html>