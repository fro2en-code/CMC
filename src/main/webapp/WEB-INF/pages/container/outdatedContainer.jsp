<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <title>过时器具列表</title>
    <style type="text/css">
    </style>
  </head>
<body>
    <%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
    <div class="layui-form layui-search-form myLabelWidth80">
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">EPC编号</label>
                <div class="layui-input-inline">
                    <input type="text" name="epcId" lay-verify="required" autocomplete="on" class="layui-input">
                </div>
                <label class="layui-form-label">印刷编号</label>
                <div class="layui-input-inline">
                    <input type="text" name="printCode" id="printCode" lay-verify="required" autocomplete="on" class="layui-input">
                </div>
                <label class="layui-form-label">器具类型</label>
                <div class="layui-input-inline">
                    <select name="containerTypeName" lay-filter="containerTypeNameFilter" lay-search>
                        <option value="">请选择</option>
                    </select>
                </div>
            </div>
            <div class="layui-inline">
                <label class="layui-form-label">器具代码</label>
                <div class="layui-input-inline">
                    <select name="containerCode" lay-filter="containerCodeFilter" lay-search>
                        <option value="">请选择</option>
                    </select>
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
                <button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
                <button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
                <cmc:button buttonId="21"> <!-- 用户有该按钮权限才会显示以下代码 -->
                	<button class="layui-btn layui-btn-sm exportButton" data-type="reload">导出当前查询结果</button>
                </cmc:button>
            </div>
        </div>
        <table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
    </div>

        <%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
</body>
  <script type="text/javascript">
      layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element', 'form', 'layedit'], function () {
          var table = layui.table;
          var layer = layui.layer;
          var form = layui.form;
          var upload = layui.upload;
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

          //初始化器具代码select框
          $.ajax({
              type: 'POST',
              url: basePath + '/containerCode/listAllContainerCode',
              dataType: 'json',
              success: function (data) {
                  var contain = data.list;
                  var option;
                  for (var i=0;i<contain.length;i++){
                      option += "<option value='" + contain[i].containerCode + "'>" + contain[i].containerCode+ "</option>";
                  }
                  layui.$(".layui-search-form select[name='containerCode']").append(option);
                  form.render();
              }
          });

          //初始化器具类型select框
          $.ajax({
              type: 'POST',
              url: basePath + '/containerType/listAllContainerType',
              dataType: 'json',
              success: function (data) {
                  var options = "";
                  for (var i=0;i<data.length;i++){
                      options += "<option value='" + data[i].containerTypeId + "'>" + data[i].containerTypeName+ "</option>";
                  }
                  layui.$(".layui-search-form select[name='containerTypeName']").append(options);
                  form.render();
              }
          });

          layer.load(2); //加载中loading效果
          //方法级渲染
          table.render({
              elem: '#LAY_TABLE'
              , loading: true
              ,done : function(res, curr, count) {
              		layer.closeAll('loading'); //关闭加载中loading效果
          	  }
              , url: basePath + '/container/pagingOutmodeContainer' //数据接口
              , cols: [[
                  {field: 'epcId', title: 'EPC编号', width: 180, sort: false}
                  , {field: 'printCode', title: '印刷编号', width: 140, sort: false}
                  , {field: 'containerCode', title: '器具代码', width: 140, sort: false}
                  , {field: 'containerTypeName', title: '器具类型',width: 140, sort: false}
                  , {field: 'containerName', title: '器具名称',width: 140, sort: false}
                  , {field: 'contractNumber', title: '合同号',width: 140, sort: false}
                  , {field: 'receiveNumber', title: '领用单号',width: 140, sort: false}
                  , {
                      field: 'updateTimeAndName', title: '操作人与操作时间',width: 250
                      , templet: function (d) {
                          if (d.modifyTime == null && d.modifyRealName == null) {
                              return "";
                          } else {
                              return d.modifyRealName + '&nbsp;' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.modifyTime);
                          }
                      }
                  }
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
                  , msgName: 'msg' //状态信息的字段名称，默认：msg
                  , countName: 'total' //数据总数的字段名称，默认：count
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

          layui.$('.queryButton').on('click', function () {
        	  layer.load(2); //加载中loading效果
              tableReload.reload();
          });

          //列表数据重载
          var tableReload = {
              reload: function () {  //执行重载
                  table.reload('layuiReloadId', {
                      page: { curr: 1 },  //重新从第 1 页开始
                      where: {
                          'epcId': $(".layui-search-form input[name='epcId']").val(),
                          'printCode': $(".layui-search-form input[name='printCode']").val(),
                          'containerTypeId': $(".layui-search-form select[name='containerTypeName']").val(),
                          'containerCode': $(".layui-search-form select[name='containerCode']").val(),
                          'startDate': $(".layui-search-form input[name='startDate']").val(),
                          'endDate': $(".layui-search-form input[name='endDate']").val()
                      }
                  });
              }
          };
        //重置按钮
          layui.$('.resetButton').on('click', function(){
            $(".layui-search-form input[name='epcId']").val("");
          	$(".layui-search-form input[name='printCode']").val("");
          	$(".layui-search-form input[name='startDate']").val("");
          	$(".layui-search-form input[name='endDate']").val("");
          	$(".layui-search-form select[name='containerTypeName']").val(""); 
      	    $(".layui-search-form select[name='containerCode']").val("");
      	  	form.render();
          });
          layui.$('.exportButton').on('click', function () {
              var epcId = $(".layui-search-form input[name='epcId']").val();
              var printCode = $(".layui-search-form input[name='printCode']").val();
              var containerTypeId = $(".layui-search-form select[name='containerTypeName']").val();
              var containerCode = $(".layui-search-form select[name='containerCode']").val();
              var startDate = $(".layui-search-form input[name='startDate']").val();
              var endDate = $(".layui-search-form input[name='endDate']").val();
              var param = "?a=1";
              if (epcId) {
                  param += "&epcId=" + epcId;
              }
              if (printCode) {
                  param += "&printCode=" + printCode;
              }
              if (containerTypeId) {
                  param += "&containerTypeId=" + containerTypeId;
              }
              if (containerCode) {
                  param += "&containerCode=" + containerCode;
              }
              if (startDate) {
                  param += "&startDate=" + startDate;
              }
              if (endDate){
                  param+="&endDate="+endDate;
              }
              window.open(basePath + '/container/expertToExcel' + param, 'target', '');
          });
      });
  </script>
</html>