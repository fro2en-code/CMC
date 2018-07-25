<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.cdc.cdccmc.common.util.StatusCode"%>
<%@ include file="common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>无菜单权限</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div style="color: red;">
		<%--   当前仓库任何菜单权限，请联系管理员！或切换至另一个仓库。 --%>
		<%= StatusCode.STATUS_103_MSG %>
	</div>

	<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>
</html>