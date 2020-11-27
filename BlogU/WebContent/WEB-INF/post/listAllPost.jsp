<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	#memberPhoto {
		width:5vw;
		height:5vh;
	}
</style>
</head>
<body>
<a href="<%=request.getContextPath()%>/post/addPost">New Post</a>
${actionStatus}

<a href="<%=request.getContextPath() %>/member/listOneMember/${sessionScope.memberId}">Back to profile
	<img src="<%=request.getContextPath() %>/resources/img/cat.png" title="Back to profile" alt="Back to profile">
</a>
<table>
	<c:forEach var="postVO" items="${postsByMemberId}">
		<tr>
			<td>
			<h1>${postVO.title}</h1>
			<br>
			<h3>${postVO.postDate}</h3>
			<hr>
			<h5>${postVO.content}</h5>	
			
			<a href="<%=request.getContextPath() %>/post/getOneForList/${postVO.postId}">read more...</a>
			
			<form action="<%=request.getContextPath()%>/post/delete" method="POST">
				<input type="hidden" name="postId" value="${postVO.postId}"/>
				<input type="hidden" name="memberId" value="${postVO.memberVO.memberId}"/>
				<input type="submit" value="刪除" onclick="return confirm('確定刪除?')"/>				
			</form>
			<form action="<%=request.getContextPath()%>/post/getOneForUpdate" method="POST">
				<input type="hidden" name="postId" value="${postVO.postId}"/>
				
				<input type="submit" value="修改" onclick="return confirm('確定修改?')"/>				
			</form>
			</td>
		</tr>
	</c:forEach>
</table>
</body>
</html>