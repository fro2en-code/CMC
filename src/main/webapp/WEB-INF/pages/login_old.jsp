<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
 String path = request.getContextPath();
 String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path;
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta content="text/html;charset=UTF-8" />
<script
	src="<%=basePath%>/static/bootstrap-3.3.7/js/jquery-3.2.1.min.js"></script>
<script src="<%=basePath%>/static/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<link href="<%=basePath%>/static/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" type="text/css" />
<style type="text/css">
body {
	font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
	font-size: 14px;
	line-height: 20px;
	color: #333;
}

.menu2 {
	padding-left: 15px;
}

.table-header {
	background-color: #E0EEE0;
}
</style>
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li>首页jsp -login 456 -- ${myname }</li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</nav>
	<div class="container">

		<div class="starter-template">
			<h2>使用账号密码登录jsp</h2>
			<form name="form" action="<%=basePath%>/login" method="POST">
				<!-- 3 -->
				<div class="form-group">
					<label for="username">账号</label> <input type="text"
						class="form-control" name="username" value="${username }"
						placeholder="账号" />
				</div>
				<div class="form-group">
					<label for="password">密码</label> <input type="password"
						class="form-control" name="password" value="${password }"
						placeholder="密码" />
				</div>
				<div class="form-group">
					<p style="color: red">${errmsg }</p>
				</div>
				<input type="submit" id="login" value="Login"
					class="btn btn-primary" />
			</form>
		</div>
	</div>
</body>
</html>