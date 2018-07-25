<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>用户错误日志</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="demoTable layui-form layui-search-form myLabelWidth90">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">用户登录名</label>
				<div class="layui-input-inline">
					<select name="accountSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton"
					data-type="reload">查询</button>
					<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
			</div>
		</div>
	</div>

	<table class="layui-hide" id="LAY_TABLE" lay-filter="myTableLayFilter"></table>
	<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>
<script type="text/javascript">
layui.use(['laydate', 'layer', 'table', 'element','form','layedit'], function(){
	  var table = layui.table;
	  var layer = layui.layer;
	  var form = layui.form;
	    var element = layui.element;
	    initLeftmenu(element); //初始化左边菜单导航栏

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
	//重置按钮
	    $('.resetButton').on('click', function(){
	    	  $(".layui-search-form select[name='accountSelect']").val("");
	    	  form.render();
	    });
	
	  layer.load(2); //加载中loading效果
	  //方法级渲染
	  var myLayTable = table.render({
	    elem: '#LAY_TABLE'
	    ,loading:true 
        ,done : function(res, curr, count) {
        	layer.closeAll('loading'); //关闭加载中loading效果
    	}
	    ,url:  basePath + '/log/pagingLogError' //数据接口
	    ,cols: [[
		      {field: 'errorContent', title: '错误日志代码'}
		      ,{field: 'errorEvent', title: '错误事件', sort: false}
		      ,{field: 'errorCode', title: '异常代码', width:120, sort: false}
		      ,{field: 'orgName', title: '仓库', sort: false}
		      ,{field: 'account', title: '操作用户', width:120, sort: false}
		      ,{field: 'createTime', title: '操作时间', width:200, sort: false,templet: function(obj){
		    	    var result = '';
		    	    if(obj.createTime){
		    	    	result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
		    	    }
		        	return result;
  			 }}
	    ]]
	    ,id: 'layuiTableId'
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
		        ,limits: [10, 30, 100] //支持每页数据条数选择
		        ,groups: 10 //显示 10 个连续页码
		        ,jump: function(obj){
		        }
		    } 
	  });
	  
	  //列表数据重载
	  var tableReload = {
	    reload: function(){  //执行重载
	      table.reload('layuiTableId', {
	        page: { curr: 1 },  //重新从第 1 页开始
	        where: {
	           'selectAccount':$("select[name='accountSelect']").val()
	        }
	      });
	    }
	  };
	  
	  //点击查询按钮
	  layui.$('.demoTable .queryButton').on('click', function(){
		    layer.load(2); //加载中loading效果
		  	tableReload.reload();
	  });
});
</script>
</html>