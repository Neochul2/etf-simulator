<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>오류</title>
</head>
<body>
<h3>오류 발생</h3>
<pre>
Exception: <%= exception != null ? exception.getMessage() : "null" %>
<% if(exception != null) { exception.printStackTrace(new java.io.PrintWriter(out)); } %>
</pre>
</body>
</html>