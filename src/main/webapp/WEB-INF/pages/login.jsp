<%@page import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
 String path = request.getContextPath();
 String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path;
%>
<!DOCTYPE html>
<html>
<head>
<style type="text/css">
#body {
	margin: 0 auto;
	width: 1366px;
	height: 667px;
	background: url('<%=basePath%>/static/img/login/banner.png') no-repeat
		center center;
	position: relative;
}
</style>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>/static/css/login.css" />
<title>中久物流-CMC系统</title>
<script
	src="<%=basePath%>/static/bootstrap-3.3.7/js/jquery-3.2.1.min.js"></script>
<link href="<%=basePath%>/static/layui-v2.2.5/css/layui.css"
	rel="stylesheet" type="text/css" />
<script src="<%=basePath%>/static/layui-v2.2.5/layui.js"></script>
<script type="text/javascript">
var showMsg = function(msg){
	layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element'], function(){
		layer.msg(msg);
	});
}
//如果后台有错误信息返回，第一时间弹出提示
var returnErrmsg = '${errmsg }';
if(returnErrmsg){
	showMsg(returnErrmsg);
}

	$(function() {
		$('#sub').bind('click', login);
	});
	document.onkeydown = function(e) {
		if (!e)
			e = window.event;//火狐中是 window.event
		if ((e.keyCode || e.which) == 13) {
			 login();
		}
	};
	function login(){
		var username = $.trim($(':text[name=username]').val());
		var password = $.trim($(':password[name=password]').val());
		var authVal = $.trim($(':text[name=code]').val());
		if ('' == username) {
			showMsg('账号不能为空');
			$(':text[name=username]').focus();
			return;
		}
		/* 正式上线时放开此代码
		if ('' == password) {
			showMsg('密码不能为空');
			$(':password[name=password]').focus();
			return;
		}
		if ('' == authVal) {
			showMsg('验证码不能为空');
			$(':text[name=code]').focus();
			return;
		} */
		$("#loginForm").attr('action','<%=basePath%>/login?t=' + new Date().getTime());    //通过jquery为action属性赋值
        $("#loginForm").submit();    //提交ID为myform的表单
	}
</script>
</head>
<body>
	<form id="loginForm" action="" method="post">
		<div id="head">
			<div id="head_div">
				<div class="">
					<img src="<%=basePath%>/static/img/login/logo.png" alt=""/>
				</div>
			</div>
		</div>
		<div id="body">
			<div id="box">
				<div id="box_box">
					<div class="user">
						账 &nbsp;&nbsp;号 <input type="text" name="username"
							value="${username }" />
					</div>
					<div class="user">
						密 &nbsp;&nbsp;码 <input type="password" name="password"
							value="${password }" />
					</div>
					<div class="user">
						验证码<input id="text" type="text" name="code"
							onkeyup="value=value.replace(/[\W]/g,'')">
						<img src="<%=basePath%>/authcode?t=<%=new Date().getTime()%>"
						onclick="this.src='<%=basePath%>/authcode?t=' + (new Date()).getTime()"
						style="cursor: pointer;width: 120px;height: 25px;vertical-align: middle;background-color:Transparent;" title="看不清?再点击一下呗" >
					</div>
				</div>
			</div>
			<a href="javascript:;" id="sub">登 录</a>
		</div>
	</form>
</body>
</html>