<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta content="text/html;charset=UTF-8" />
<title>登录超时</title>
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
	background-color: #CCFFCC;
}
#num{
	font-size: 28px;padding: 0px 25px 0px 25px;color: red;
}
</style>
</head>

<body>
	<div style="text-align: center;font-size: 22px;margin-top: 80px;">
		<img src="<%=basePath%>/static/img/login/logo.png" alt="" />
		<div style="margin-top: 50px;">
			登录超时！将在
			<a href="<%=basePath%>/login" id="num">3</a>
			秒后进入登录页面！
		</div>
		<div style="margin-top: 50px;color: #006666;">
			<a href="<%=basePath%>/login" >立即跳转至首页</a>
		</div>
	</div>
	 
</body>
<script type="text/javascript">  
		var sec = 2;
		var num = document.getElementById("num");
	    window.onload=function(){         //一进该页面就加载以下方法  
		    setInterval('countDown()',1000);    //一般秒设置为参数为1000  
	    }
                          //设置倒计时时间为30秒  
        function countDown() {        //倒计时的方法  
            if(sec > 0) {
                num.innerHTML = sec--;  
            } else {  
                location = "<%=basePath%>/login";  //倒计时为0进入的页面  
            }  
        }  
</script> 
</html>