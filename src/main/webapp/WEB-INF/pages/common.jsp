<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cmc" uri="/cdccmc/cmc-taglib"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
	+ path;
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta content="text/html;charset=UTF-8" />
<script src="<%=basePath%>/static/bootstrap-3.3.7/js/jquery-3.2.1.min.js"></script>
<%-- bootstrap组件 --%>
<script src="<%=basePath%>/static/bootstrap-3.3.7/js/bootstrap.js"></script>
<link href="<%=basePath%>/static/bootstrap-3.3.7/css/bootstrap.css" rel="stylesheet" type="text/css" />
<script	src="<%=basePath%>/static/bootstrap-3.3.7/js/bootstrap-multiselect.js"></script>
<link href="<%=basePath%>/static/bootstrap-3.3.7/css/bootstrap-multiselect.css" rel="stylesheet" type="text/css" />
<%-- layui组件 --%>
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link href="<%=basePath%>/static/layui-v2.2.5/css/layui.css" rel="stylesheet" media="all" />
<script src="<%=basePath%>/static/layui-v2.2.5/layui.js"></script>
<%-- 自定义组件和样式 --%>
<script src="<%=basePath%>/static/js/date.util.js"></script>
<script src="<%=basePath%>/static/js/index.js"></script>
<link href="<%=basePath%>/static/css/index.css" rel="stylesheet" type="text/css" />
<style type="text/css">

</style>
</head>
<script type="text/javascript">
var basePath = "<%=basePath%>";
</script>
<%@ include file="header.jsp"%>
<%@ include file="leftmenu.jsp"%>