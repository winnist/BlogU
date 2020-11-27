<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>



<!DOCTYPE html>
<html>
<head>

<title>Insert title here</title>
</head>
<body>
<h1>${loginMsg}</h1>
${message}

<form action="${pageContext.request.contextPath}/login" method="POST">
	<table>
		<tr>
			<td width="10%">帳號:</td>
			<td width="55%"><input type="email" name="email" id="email"/></td>
		</tr>
		<tr>
			<td width="10%">密碼:</td>
			<td width="50%"><input type="password" name="password" id="password" placeholder="請輸入密碼"/></td>
	
		</tr>
	
	</table>
	
		<input type="submit" value="登入" />
		
		<hr>
		<a href="<%=request.getContextPath()%>/member/addMember">註冊</a>
</form>


</body>
</html>