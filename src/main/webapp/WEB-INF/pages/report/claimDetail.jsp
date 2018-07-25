<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>索赔明细</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="layui-form layui-search-form myLabelWidth80">
		<div class="layui-form-item">
			<label class="layui-form-label" style="width:180px;">盘点单号或包装流转单号</label>
			<div class="layui-input-inline">
				<input type="text" class="layui-input" name="orderCode" placeholder="不支持模糊查询">
			</div>
			<label class="layui-form-label">开始日期</label>
			<div class="layui-input-inline">
				<input type="text" class="layui-input" id="startDate" name="startDate">
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">结束日期</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" id="endDate" name="endDate">
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<cmc:button buttonId="40"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm exportButton"
					data-type="reload">导出当前查询结果</button>
				</cmc:button>
			</div>
		</div>
		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

		<%------------------------------------------------
                                右边内容结束
            ------------------------------------------------%>

	</div>
</body>

<script type="text/javascript">
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element', 'form', 'layedit'], function () {
          var table = layui.table;
          var layer = layui.layer;
          var form = layui.form;
          var upload = layui.upload;
          var laydate = layui.laydate;
          var element = layui.element;
          initLeftmenu(element); //初始化左边菜单导航栏
          //时间选择器
          layui.laydate.render({
              elem: '#startDate'
              , type: 'datetime'
              , min: '2018-01-01'
          });
          //时间选择器
          layui.laydate.render({
              elem: '#endDate'
              , type: 'datetime'
              , min: '2018-01-01'
          });
          
          layer.load(2); //加载中loading效果
          //方法级渲染
          table.render({
              elem: '#LAY_TABLE'
              , loading: true
              ,done : function(res, curr, count) {
              		layer.closeAll('loading'); //关闭加载中loading效果
          	  }
              , url: basePath + '/claimDetail/pagingClaimDetail' //数据接口
              , cols: [[
                  {field: 'claimType', title: '索赔来源', width: 140, templet: function (d) {
                          if (d.claimType == null) {
                              return "";
                          } else if (d.claimType == 1){
                              return '<span> 流转单差异索赔 </span>';
                          }else if (d.claimType == 2){
                              return '<span>盘点丢失索赔</span>';
                          }
                   }}
                  , {field: 'orderCode', title: '盘点单号或包装流转单号', width: 220, sort: false, templet: function (d) {
	                      if (d.claimType == "1"){
	                          return d.orderCode;
	                      }else if (d.claimType == "2"){
	                          return d.inventoryId;
	                      }
	                      return "";
	               }}
                  , {field: 'epcId', title: 'EPC编号', width: 180, sort: false}
                  , {field: 'containerCode', title: '器具代码', width: 150, sort: false}
                  , {field: 'containerTypeName', title: '器具类型',width: 120, sort: false}
                  , {field: 'differenceNumber', title: '数量',width: 100, sort: false}
                  , {field: 'remark', title: '备注', width: 140, sort: false}
                  , {field: 'createTime', title: '日期', width: 240
                      , templet: function (d) {
                          if (d.createTime == null ) {
                              return "";
                          } else {
                              return '<span>' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime) + '</span>';
                          }
                      }
                  }
                  , {field: 'createOrgName', title: '创建仓库', width: 140, sort: false}
                  , {field: 'createRealName', title: '创建人', width: 120, sort: false}
              ]]
              , id: 'layuiReloadId'
              , page: true
              //,height: 315
              , request: {
                  pageName: 'currentPage' //页码的参数名称，默认：page
                  , limitName: 'pageSize' //每页数据量的参数名，默认：limit
              },
              response: {
                  statusName: 'status' //数据状态的字段名称，默认：code
                  , statusCode: 200 //成功的状态码，默认：0
                  , msgName: 'msg', //状态信息的字段名称，默认：msg
                  countName: 'total' //数据总数的字段名称，默认：count
                  , dataName: 'data' //数据列表的字段名称，默认：data
              }
              , page: {
                  elem: 'tablePaging', count: 0,
                  first: '首页', last: '尾页', prev: '上一页', next: '下一页'
                  , layout: ['count', 'limit', 'prev', 'page', 'next', 'skip'] //分页条按钮排序
                  , curr: 1 //设定初始在第 1 页
                  , limit: 10  //每页多少条
                  , limits: [10, 30, 100] //支持每页数据条数选择
                  , groups: 10 //显示 10 个连续页码
                  , jump: function (obj) {
                  }
              }  //开启分页

          });
          //列表数据重载
          var tableReload = {
              reload: function () {  //执行重载
                  table.reload('layuiReloadId', {
                      page: { curr: 1 },  //重新从第 1 页开始
                      where: {
                          'orderCode': $(".layui-search-form input[name='orderCode']").val(),
                          'startDate': $(".layui-search-form").find("input[name='startDate']").val(),
                          'endDate': $(".layui-search-form").find("input[name='endDate']").val()
                      }
                  });
              }
          };
          layui.$('.queryButton').on('click', function () {
        	  layer.load(2); //加载中loading效果
              tableReload.reload();
          });
        //重置按钮
          layui.$('.resetButton').on('click', function(){
          	  $(".layui-search-form input[name='orderCode']").val("");
          	  $(".layui-search-form input[name='startDate']").val("");
          	  $(".layui-search-form input[name='endDate']").val("");
          });
          layui.$('.exportButton').on('click', function () {
              var orderCode = $(".layui-search-form input[name='orderCode']").val();
              var startDate = $(".layui-search-form input[name='startDate']").val();
              var endDate = $(".layui-search-form input[name='endDate']").val();
              var param = "?a=1";
              if (orderCode) {
                  param += "&orderCode=" + orderCode;
              }
              if (startDate) {
                  param += "&startDate=" + startDate;
              }
              if (endDate){
                  param+="&endDate="+endDate;
              }
              window.open(basePath + '/claimDetail/expertToExcel' + param, 'target', '');
          });
      });
 </script>
</html>