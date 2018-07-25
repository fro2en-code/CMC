<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>器具丢失</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="layui-form layui-search-form" id="lostContainerPanel">
		<div class="layui-form-item">
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
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="44"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm exportButton"
					data-type="reload">导出当前查询结果</button>
				</cmc:button>
			</div>
		</div>
	</div>

	<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
	<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
	</div>
	</div>
</body>
<script type="text/html" id="editClaimPanel">
      <form class="layui-form editClaimPanelForm " action="">
          <div class="layui-form-item">
          </div>
          <div class="layui-form-item" >
              <label class="layui-form-label" style="width:150px;">索赔状态<span class="myRedColor">*</span></label>
              <div class="layui-input-inline">
                	<select name="claimTypeName"  lay-filter="selectNameFilter"  lay-search="">
                    	<option value="">直接选择或搜索选择</option>
                	</select>
              </div>
          </div>
      </form>
</script>

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
        
        layer.load(2); //加载中loading效果
        //方法级渲染
        table.render({
            elem: '#LAY_TABLE'
            ,loading:true
            ,url:  basePath + '/lostContainer/pagingLostContainer' //数据接口
            ,done : function(res, curr, count) {
            	layer.closeAll('loading'); //关闭加载中loading效果
            	form.render();
        	}
            ,cols: [[
            	 {type:'numbers',title: '序号',width:50, sort: false}
                ,{field: 'epcId', title: 'EPC编号', width:130, sort: false}
                ,{field: 'printCode', title: '印刷编号', width:130, sort: false}
                ,{field: 'containerCode', title: '器具代码', width:130, sort: false}
                ,{field: 'containerTypeName', title: '器具类型', width:130, sort: false}
                ,{field: 'inventoryId', title: '盘点编号', width:200, sort: false}
                ,{field: 'createOrgName', title: '创建仓库', width:200, sort: false}
                ,{field: 'createTime', title: '丢失时间', width:160, sort: false
                	, templet: function (d) {
                        if (d.createTime == null) {
                            return "";
                        } else {
                            return '<span>' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime) + '</span>';
                        }
                    }
                }
                ,{field: 'createRealName', title: '丢失确认人', width:110, sort: false}
                ,{field: 'lostRemark', title: '丢失备注', width:110, sort: false}
                ,{field: 'isClaim', title: '索赔状态', width:150, sort: false,templet: function(d){
                    if(d.isClaim == '1'){
                        return "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#xe618;</i>";
                    }else{
                        return '<a class="layui-btn layui-btn-xs" lay-event="setClaim">索赔</a>';
                    }
                }}
                ]]
            ,id: 'layuiReloadId'
            ,page: true
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
                    	'startDate':$(".layui-search-form").find("input[name='startDate']").val(),
                        'endDate':$(".layui-search-form").find("input[name='endDate']").val(),
                    }
                });
            }
        };
      //重置按钮
        layui.$('.resetButton').on('click', function(){
        	  $("#lostContainerPanel input[name='startDate']").val("");
        	  $("#lostContainerPanel input[name='endDate']").val("");
        });
        layui.$('.queryButton').on('click', function(){
        	layer.load(2); //加载中loading效果
        	tableReload.reload();
        });
        layui.$('.exportButton').on('click', function () {
            var startDate = $(".layui-search-form input[name='startDate']").val();
            var endDate = $(".layui-search-form input[name='endDate']").val();
            var param = "?a=1";
            if (startDate) {
                param += "&startDate=" + startDate;
            }
            if (endDate){
                param+="&endDate="+endDate;
            }
            window.open(basePath + '/lostContainer/expertToExcel'+param,'target','');
        });
        
        var claimTypes; 
        $.ajax({
            type: 'POST',
            url: basePath +'/claim/listClaimType',
            dataType: 'json',
            success: function(data){
                if(data.status == 200){
                	claimTypes = data.list;
                    form.render();
                }
            }
        });
        

        //监听工具条,点击table表格里的索赔按钮
        table.on('tool(myLayFilter)', function(obj){
            var data = obj.data;
            layer.confirm("确认要索赔器具["+data["epcId"]+"]吗？", { title: "索赔确认" }, function (index) {
            	var param = {};
            	param["containerLostId"] = data.containerLostId;
            	$.ajax({
                    url: basePath + "/lostContainer/setIsClaim",
                    type:"POST",
                    dataType: "json",
                    data: param,
                    success: function(result, textStatus, jqXHR){
                    	layer.msg(result.msg);
                    	if(result.status == 200){
                            layer.closeAll('page');   //成功后，关闭所有弹出框
                            tableReload.reload(); //重新加载table数据
                        }
                    }
                });
            	
            });
        });
});
</script>
</html>