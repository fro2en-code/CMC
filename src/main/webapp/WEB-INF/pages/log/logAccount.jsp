<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>用户操作日志</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="demoTable layui-form layui-search-form myLabelWidth75">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">EPC编号</label>
				<div class="layui-input-inline">
					<input type="text" name="epcId" lay-verify="required"
						autocomplete="on" class="layui-input">
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label" style="width: 95px">用户登录名</label>
				<div class="layui-input-inline">
					<select name="accountSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
					<!-- <input type="text" name="account" lay-verify="required" autocomplete="off" class="layui-input"> -->
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">开始日期</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" id="startDate"
						name="startDate">
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">结束日期</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" id="endDate" name="endDate">
				</div>
			</div>
			<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
			<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
		<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>
<script type="text/javascript">
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element'], function(){
  var table = layui.table;
  var layer = layui.layer;
  var form = layui.form;
  var element = layui.element;
  initLeftmenu(element); //初始化左边菜单导航栏

 //时间选择器
 layui.laydate.render({
    elem: '#startDate'
    ,type: 'datetime'
    ,min: '2018-01-01'
  });
 //时间选择器
 layui.laydate.render({
    elem: '#endDate'
    ,type: 'datetime'
    ,min: '2018-01-01'
  });
 
 //初始化登录名下拉选择框
 $.ajax({
 	  url: basePath + "/user/listAllUserByFiliale",
 	  type:"POST",
 	  dataType: "json",
 	  data: {},
 	  success: function(result, textStatus, jqXHR){
 		    var options = "<option value=\"\">直接选择或搜索选择</option>";
 		    var list = result.list;
	  		$.each( list, function(i, obj){
	  		  	options += "<option value=\""+obj.account+"\">"+obj.account+" ("+obj.realName+")</option>";
	  		});
	  		$("select[name='accountSelect']").html(options);
	  		form.render();
 	  }
 });	 
  
 layer.load(2); //加载中loading效果
  //方法级渲染
  table.render({
    elem: '#LAY_TABLE'
    ,loading:true 
    ,done : function(res, curr, count) {
    	layer.closeAll('loading'); //关闭加载中loading效果
	}
    ,url:  basePath + '/log/pagingLogAccount' //数据接口
    ,cols: [[
             {field: 'account', title: '登录名', width:140, sort: false}
	      ,{field: 'logContent', title: '日志内容', sort: false}
	      /* ,{field: 'epcId', title: 'EPC编号', width:200, sort: false} */
	      ,{field: 'createTime', title: '时间', width:200, sort: false, templet: function (obj) {
		  		var result = '';
		  		if(obj.createTime){
		  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
		  		}
	        	return result;
	        }} 
	      ,{field: 'orgName', title: '仓库', width:200, sort: false}
    ]]
    ,id: 'layuiReloadId'
    ,page: true
    //,height: 315
    ,request: {
	  	  pageName: 'currentPage' //页码的参数名称，默认：page
	  	  ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
	  },
	  response: {
	  	  statusName: 'status' //数据状态的字段名称，默认：code
	  	  ,statusCode: 200 //成功的状态码，默认：0
	  	  ,msgName: 'msg', //状态信息的字段名称，默认：msg
	  	  countName: 'total' //数据总数的字段名称，默认：count
	  	  ,dataName: 'data' //数据列表的字段名称，默认：data
	  }
	  ,page: {
	        elem: 'tablePaging',count: 0,
	        first: '首页',last: '尾页',prev:'上一页',next:'下一页'
	        ,layout: ['count','limit', 'prev', 'page', 'next', 'skip'] //分页条按钮排序
	        ,curr: 1 //设定初始在第 1 页
	        ,limit: 10  //每页多少条
	        ,limits: [10,20, 30, 100] //支持每页数据条数选择
	        ,groups: 10 //显示 10 个连续页码
	        ,jump: function(obj){
	        }
	    }  //开启分页
	    
  });
  
  var tableReload = {
    reload: function(){  //执行重载
      table.reload('layuiReloadId', {
        page: { curr: 1 }  //重新从第 1 页开始
        ,where: {
           'epcId':$(".layui-search-form input[name='epcId']").val()
           ,'selectAccount':$(".layui-search-form select[name='accountSelect']").val()
           ,'startDate':$(".layui-search-form input[name='startDate']").val()
           ,'endDate':$(".layui-search-form input[name='endDate']").val()
        }
      });
    }
  };
  
//重置按钮
  layui.$('.resetButton').on('click', function(){
  	  $(".layui-search-form input[name='epcId']").val("");
  	  $(".layui-search-form select[name='accountSelect']").val("");
  	  $(".layui-search-form input[name='startDate']").val("");
  	  $(".layui-search-form input[name='endDate']").val("");
  	  form.render();
  });
  layui.$('.layui-search-form .queryButton').on('click', function(){
	    layer.load(2); //加载中loading效果
  		tableReload.reload();
  });
});
</script>
</html>