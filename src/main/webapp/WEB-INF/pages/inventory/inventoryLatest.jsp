<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>当前库存</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="layui-form layui-search-form myLabelWidth80" action="">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:85px;">仓库名称</label>
				<div class="layui-input-inline">
					<select name="targetOrgId" lay-search>
						<option value="">请选择</option>
					</select>
				</div>
				<label class="layui-form-label">器具类型</label>
				<div class="layui-input-inline">
					<select name="containerType" lay-search="">
						<option value="">请选择</option>
					</select>
				</div>
				<label class="layui-form-label">器具代码</label>
				<div class="layui-input-inline">
					<select name="containerCode" lay-search="">
						<option value="">请选择</option>
					</select>
				</div>
				<button class="layui-btn layui-btn-sm queryButton"
					data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="80"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm exportButton" data-type="reload">导出当前查询结果</button>
				</cmc:button>
			</div>
			<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

			<script type="text/html" id="bar">
        <a class="layui-btn layui-btn-xs" lay-event="defaultArea">设置为默认入库区域</a>
    </script>
			<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
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
       
        //初始化器具代码select框
        $.ajax({
            type: 'POST',
            url: basePath + '/containerCode/listAllContainerCode',
            dataType: 'json',
            success: function (data) {
                if (data.status == 200 && data.list != null) {
                    var contain = data.list;
    	  		    var options = "<option value=\"\">直接选择或搜索选择</option>";
                    for (var i=0;i<data.list.length;i++){
                    	options += "<option value='" + contain[i].containerCode + "'>" + contain[i].containerCode + "</option>";
                    }
                    $(".layui-search-form select[name='containerCode']").html(options);
                    form.render();
                } else {
                    layer.msg(data.msg);
                }
            }
        });
        
        //初始化器具类型select框
        $.ajax({
            type: 'POST',
            url: basePath + '/containerType/listAllContainerType',
            dataType: 'json',
            success: function (data) {
	  		    var options = "<option value=\"\">直接选择或搜索选择</option>";
                for (var i=0;i<data.length;i++){
                	options += "<option value='" + data[i].containerTypeId + "'>" + data[i].containerTypeName+ "</option>";
                }
                $(".layui-search-form select[name='containerType']").html(options);
                form.render();
            }
        });

        //初始化仓库select框
		$.ajax({
			type: 'POST',
			url: basePath + '/org/filialeSystemOrgList',
			dataType: 'json',
			success: function (data) {
				var orgList = data.list;
				var orgOption = "<option value=''>直接选择或搜索选择</option>";
				for (var i=0;i<orgList.length;i++){
					orgOption += "<option value='" + orgList[i].orgId + "'>" + orgList[i].orgName+ "</option>";
				}
				$(".layui-search-form select[name='targetOrgId']").html(orgOption);
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
            ,url:  basePath + '/inventory/pagingInventoryLatest'
            ,cols: [[
	               {type:'numbers',title: '序号',width:60, sort: false}
	              ,{field: 'orgName', title: '仓库名称', width:250, sort: false}
	              ,{field: 'containerCode', title: '器具代码', width:100, sort: false}
	              ,{field: 'containerTypeName', title: '器具类型', width:130, sort: false}
	              ,{field: 'receiveNumber', title: '收货数量', width:100, sort: false}
	              ,{field: 'sendNumber', title: '发货数量', width:100, sort: false}
	              ,{field: 'inOrgNumber', title: '在库数量', width:100, sort: false}
	              , {field: 'createTime', title: '创建时间', width: 250
	                  , templet: function (obj) {
	      	  	  		var result = '';
	      		  		if(obj.createTime){
	      		  			result += '<span class="tableCellPre">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
	      		  		}
	      	        	return result;
	                  }
	              }
	              ,{field: 'orderCode', title: '流转单号', width:250, sort: false}
	              ,{field: 'remark', title: '备注', width:250, sort: false}
               ,{field: 'createRealName', title: '创建人', width:150, sort: false}
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
                        'containerTypeId': $(".layui-search-form select[name='containerType']").val(),
                        'containerCodeId': $(".layui-search-form select[name='containerCode']").val(),
                        'targetOrgId': $(".layui-search-form select[name='targetOrgId']").val()
                    }
                });
            }
        };
        layui.$('.layui-search-form .queryButton').on('click', function(){
        	layer.load(2); //加载中loading效果
        	tableReload.reload();
        });
      //重置按钮
        $('.resetButton').on('click', function(){
        	  $(".layui-search-form select[name='containerType']").val(""); 
        	  $(".layui-search-form select[name='containerCode']").val("");
        	  $(".layui-search-form select[name='targetOrgId']").val("");
        	  form.render();
        });
    layui.$('.exportButton').on('click', function () {
        var containerTypeId = $(".layui-search-form select[name='containerType']").val();
        var containerCodeId = $(".layui-search-form select[name='containerCode']").val();
        var param = "?a=1";
        if (containerTypeId) {
            param += "&containerTypeId=" + containerTypeId;
        }
        if (containerCodeId){
            param+="&containerCodeId="+containerCodeId;
        }
        window.open(basePath + '/inventory/expertExcelForInventoryLatest' +param,'target','');
    });
});
</script>
</html>