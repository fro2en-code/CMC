<%@page import="org.springframework.util.CollectionUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.cdc.cdccmc.domain.sys.SystemUser"%>
<%@ page import="com.cdc.cdccmc.domain.sys.SystemMenuWeb"%>
<%@ page import="com.cdc.cdccmc.common.util.SysConstants"%>
<div class="layui-fluid" style="padding-left: 0px;">
<div class="layui-row">
	<div class="layui-col-md2" > <!-- style="width: 15.5%;" -->
		<%------------------------------------------------ 
        					左边菜单内容开始 
        ------------------------------------------------%>
		<ul id="leftMenuId" class="layui-nav layui-nav-tree layui-inline"
			lay-filter="leftMenuId"
			style="margin-right: 10px; min-height: 600px;">
			<%-- ${sessionScope.leftmenuHtml } --%>
			<%-- 	以下为例子代码，勿删除
  <li class="layui-nav-item layui-nav-itemed">
    <a href="javascript:;">默认展开</a>
    <dl class="layui-nav-child">
      <dd><a href="javascript:;">选项一</a></dd>
      <dd><a href="javascript:;">选项二</a></dd>
      <dd><a href="javascript:;">选项三</a></dd>
      <dd><a href="">跳转项</a></dd>
    </dl>
  </li>
  <li class="layui-nav-item">
    <a href="javascript:;">解决方案</a>
    <dl class="layui-nav-child">
      <dd><a href="">移动模块</a></dd>
      <dd><a href="">后台模版</a></dd>
      <dd><a href="">电商平台</a></dd>
    </dl>
  </li>
  <li class="layui-nav-item"><a href="">云市场</a></li>
  <li class="layui-nav-item"><a href="">社区</a></li> --%>
		</ul>
<script type="text/javascript">
		//初始化左边菜单栏
		var initLeftmenu = function(elementObj){
	     	$.ajax({
				url : basePath + "/page/buildLeftmenuHtml",
				type : "POST",
				dataType : "json",
				data : {
					"currentUri" : '${currentUri}',
					"d":DateUtil.todayMillisecond()
				},
				success : function(result, textStatus, jqXHR) {
					$("#leftMenuId").html(result.msg);
					elementObj.init(); //导航的hover效果、二级菜单等功能，需要依赖element模块
					//监听导航点击
					elementObj.on('nav(leftMenuId)', function(elem) {
						layer.msg(elem.text());
					});
				}
			});
		}
		/* 原来的例子代码
		$(document).ready(function() {
				$.ajax({
					url : basePath + "/page/buildLeftmenuHtml",
					type : "POST",
					dataType : "json",
					data : {
						"currentUri" : '${currentUri}',
						"d":DateUtil.todayMillisecond()
					},
					success : function(result, textStatus, jqXHR) {
						$("#leftMenuId").html(result.msg);

						//当DOM就绪时，才渲染左边菜单点击事件
						$(document).ready(function() {
							//菜单收缩、展开初始化方法
							layui.use(['layer', 'element'], function() {
								var element = layui.element; //导航的hover效果、二级菜单等功能，需要依赖element模块
								var layer = layui.layer;
								//监听导航点击
								element.on('nav(leftMenuId)', function(elem) {
									layer.msg(elem.text());
								});
							});
						});
					}
				});

			}); */
</script>
		<%------------------------------------------------ 
        					左边菜单内容结束 
        ------------------------------------------------%>
	</div>
	<div class="layui-col-md10">