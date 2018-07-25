<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>器具流转历史</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="layui-form layui-search-form myLabelWidth75">
		<div class="layui-form-item">
			<label class="layui-form-label">流转单号</label>
			<div class="layui-input-inline">
				<input type="text" name="orderCode" autocomplete="on" class="layui-input">
			</div>
			<label class="layui-form-label">EPC编号</label>
			<div class="layui-input-inline">
				<input type="text" name="epcIdSeach" id="epcIdSeach"
					lay-verify="required" autocomplete="on" class="layui-input">
			</div>
			<label class="layui-form-label">器具代码</label>
			<div class="layui-input-inline">
				<select name="containerCode" lay-search="">
					<option value="">直接选择或搜索选择</option>
				</select>
			</div>
			<label class="layui-form-label">器具类型</label>
			<div class="layui-input-inline">
				<select name="containerTypeNameSeach" lay-search="">
					<option value="">请选择</option>
				</select>
			</div>
			<div class="layui-form-item">
				<label class="layui-form-label">操作公司</label>
				<div class="layui-input-inline">
					<select name="orgIdCompany" lay-search="">
						<option value="">请选择</option>
					</select>
				</div>
					<label class="layui-form-label">流转状态</label>
					<div class="layui-input-inline">
						<select name="circulateState" lay-search>
							<option value="">请选择</option>
						</select>
					</div>
				<label class="layui-form-label">开始日期</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" id="startDate"
						name="startDate">
				</div>
				<div class="layui-inline">
					<label class="layui-form-label">结束日期</label>
					<div class="layui-input-inline">
						<input type="text" class="layui-input" id="endDate" name="endDate">
					</div>
				</div>
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
                <cmc:button buttonId="93"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button type="button" class="layui-btn layui-btn-sm exportButton">导出当前查询结果</button>
                </cmc:button>
			</div>

			<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

			<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
		</div>
	</div>
</body>

<script type="text/javascript">
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element','form','layedit'], function(){
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
           ,type: 'datetime'
           ,min: '2018-01-01'
         });
        //时间选择器
        layui.laydate.render({
           elem: '#endDate'
           ,type: 'datetime'
           ,min: '2018-01-01'
         });
      //初始化器具代码下拉选择框
        $.ajax({
        	  url: basePath + "/containerCode/listAllContainerCode",
        	  type:"POST",
        	  dataType: "json",
        	  data: {},
        	  success: function(result, textStatus, jqXHR){
        		    var options = "<option value=\"\">直接选择或搜索选择</option>";
        		    var list = result.list;
      	  		$.each( list, function(i, obj){
      	  		  	options += "<option value=\""+obj.containerCode+"\">"+obj.containerCode+"</option>";
      	  		});
      	  		$("select[name='containerCode']").html(options);
      	  		form.render();
        	  }
        });
        
      //初始化器具类型select框
        $.ajax({
            type: 'POST',
            url: basePath + '/containerType/listAllContainerType',
            dataType: 'json',
            success: function (data) {
                var option = "";
                for (var i=0;i<data.length;i++){
                    option += "<option value='" + data[i].containerTypeId + "'>" + data[i].containerTypeName+ "</option>";
                }
                $(".layui-search-form select[name='containerTypeNameSeach']").append(option);
                form.render();
            }
        });
      //初始化操作公司select框
        $.ajax({
            type: 'POST',
            url: basePath + '/org/listAllOrg',
            dataType: 'json',
            success: function (data) {
                var option = "";
                for (var i=0;i<data.list.length;i++){
                    option += "<option value='" + data.list[i].orgId + "'>" + data.list[i].orgName+ "</option>";
                }
                $(".layui-search-form select[name='orgIdCompany']").append(option);
                form.render();
            }
        });
        
        //初始化器具流转状态select框
        $.ajax({
            type: 'POST',
            url: basePath + '/circulate/listCirculateState',
            dataType: 'json',
            success: function (data) {
            	if (data.status == 200 && data.list != null) {
                     var option="";
                    for (var i=0;i<data.list.length;i++){
                    	option += "<option value='" + data.list[i].code + "'>" + data.list[i].circulate+ "</option>";
                    }
                    $(".layui-search-form select[name='circulateState']").append(option);
                    form.render();
	            } 
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
            ,url:  basePath + '/circulate/pagingCirculateHistory' //数据接口
            ,cols: [[
                     {field: 'orderCode', title: '包装流转单号', width:180, sort: false}
                     , {field: 'creatTimeAndName', title: '创建人与创建时间', width: 250, height: 315
                         , templet: function (d) {
                             if (d.createTime == null && d.createRealName == null) {
                                 return "";
                             } else {
                                 return '<span>' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime) + '</span>&nbsp;&nbsp;&nbsp;<span>'  + d.createRealName+ '</span>';
                             }
                         }
                     }
                 ,{field: 'epcId', title: 'EPC编号', width:180, sort: false} //, fixed: 'left'
                ,{field: 'containerCode', title: '器具代码', width:90, sort: false}
                ,{field: 'containerTypeName', title: '器具类型', width:90, sort: false}
                ,{field: 'circulateStateName', title: '流转状态', width:100, sort: false}
                ,{field: 'fromOrgName', title: '来源仓库', width:120, sort: false}
                ,{field: 'orgName', title: '操作仓库', width:120, sort: false}
                ,{field: 'targetOrgName', title: '目标仓库', width:120, sort: false}
                ,{field: 'areaName', title: '区域', width:120, sort: false}
                ,{field: 'remark', title: '流转备注', width:250, sort: false}
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
                ,limits: [10, 30, 100] //支持每页数据条数选择
                ,groups: 10 //显示 10 个连续页码
                ,jump: function(obj){
                }
            }  //开启分页

        });
        //列表数据重载
        var tableReload = {
            reload: function(){  //执行重载
                table.reload('layuiReloadId', {
                    page: { curr: 1 },  //重新从第 1 页开始
                    where: {
                    	'epcId': $(".layui-search-form input[name='epcIdSeach']").val(),
                    	'containerCode':$(".layui-search-form select[name='containerCode']").val(),
                    	'containerTypeId': $(".layui-search-form select[name='containerTypeNameSeach']").val(),
                    	'orgId': $(".layui-search-form select[name='orgIdCompany']").val(),
                    	'startDate':$(".layui-search-form input[name='startDate']").val(),
                        'endDate':$(".layui-search-form input[name='endDate']").val(),
                        'orderCode':$(".layui-search-form input[name='orderCode']").val(),
                        'circulateState':$(".layui-search-form select[name='circulateState']").val()
                    }
                });
            }
        };
        //“查询”按钮点击
        layui.$('.queryButton').on('click', function(){
        	layer.load(2); //加载中loading效果
            tableReload.reload();
        });
      //重置按钮
        layui.$('.resetButton').on('click', function(){
        	  $(".layui-search-form input[name='epcIdSeach']").val("");
        	  $(".layui-search-form input[name='startDate']").val("");
        	  $(".layui-search-form input[name='endDate']").val("");
        	  $(".layui-search-form select[name='containerCode']").val("");
        	  $(".layui-search-form select[name='containerTypeNameSeach']").val("");
        	  $(".layui-search-form select[name='orgIdCompany']").val("");
        	  $(".layui-search-form input[name='orderCode']").val("")
        	  form.render();
        });


    //导出
    layui.$('.exportButton').on('click', function () {
        var param ="?a=1";
        var epcId = $(".layui-search-form input[name='epcIdSeach']").val();
        var orderCode = $(".layui-search-form input[name='orderCode']").val();
        var containerCode = $(".layui-search-form select[name='containerCode']").val();
        var containerTypeId = $(".layui-search-form select[name='containerTypeId']").val();
        var orgId = $(".layui-search-form select[name='orgIdCompany']").val();
        var circulateState = $(".layui-search-form select[name='circulateState']").val();
        var startDate = $(".layui-search-form").find("input[name='startDate']").val();
        var endTime = $(".layui-search-form").find("input[name='endTime']").val();
        if (epcId){
            param+="&epcId="+epcId;
        }
        if (orderCode) {
            param += "&orderCode=" + orderCode;
        }
        if (containerCode){
            param+="&containerCode="+containerCode;
        }
        if (containerTypeId){
            param+="&containerTypeId="+containerTypeId;
        }
        if (orgId){
            param+="&orgId="+orgId;
        }
        if (circulateState){
            param+="&circulateState="+circulateState;
        }
        if (startDate){
            param+="&startDate="+startDate;
        }
        if (endTime){
            param+="&endTime="+endTime;
        }
        console.log(circulateState);
        window.open(basePath + '/circulate/expertToExcelHistory'+param,'targer','');

    });
});
</script>
</html>