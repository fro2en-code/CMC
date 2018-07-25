 <%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <title>盘点单统计</title>
    <style type="text/css">
		.table1Column1Lable{
			font-weight: 400;width: 200px;
		}
		.redtable{
			font-weight: 700;width: 400px;color: #b92c28;	//	红色
		}
		.bluetable{
			width: 400px;font-weight: 700;color: #1E9FFF;   //蓝色
		}
		.mytable{
			width: 100px;font-weight: 700;
		}

    </style>
  </head>
  <body>

	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
    <div class="layui-form layui-search-form" id="defferenceDealPanel">
		<div class="layui-form-item">
            <div class="layui-inline">
                <div class="layui-inline">
					<label class="layui-form-label">盘点编号</label>
					<div class="layui-input-inline">
						<input type="text" class="layui-input" name="inventoryId">
					</div>
					<label class="layui-form-label">差异数</label>
					<div class="layui-input-inline">
							<select name="differentNumber" lay-filter="different" lay-search lay-verify="">
							  <option value="">请选择</option>
							  <option value="1">大于0</option>
							  <option value="2">等于0</option>
							  <option value="3">小于0</option>
							</select>  
					</div>
				</div>
                <div class="layui-inline">
	                <button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
	                <button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				</div>

				<table class="inventoryTable">
				<tbody id="inventoryInfo"> </tbody>
				</table>

				<table class="orgTable">
					<tbody id="orgInfo"> </tbody>
				</table>

				<table class="timeTable">
					<tbody id="timeInfo"> </tbody>
				</table>

				<table class="PromptTable">
					<tbody id="PromptInfo">
					<td class='mytable'>提示:</td><td class='bluetable'>系统保有量，盘点数量不包含在途数量 </td>
					</tbody>
				</table>

            </div>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
        
	</div>	
	<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>
<script type="text/javascript">
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element', 'form', 'layedit'], function () {
    var table = layui.table;
    var layer = layui.layer;
    var form = layui.form;
    var upload = layui.upload;
    var containerCoderOption;
    var element = layui.element;
    initLeftmenu(element); //初始化左边菜单导航栏
    table.render({
        elem: '#LAY_TABLE'
        ,loading:true
        ,done: function(res, curr, count){
            layer.closeAll('loading'); //关闭加载中loading效果
        }
        //,url:  basePath + '/inventoryDetail/pagingInventoryDetailSum' //数据接口
        ,cols: [[
            {type:'numbers',title: '序号',width:60, sort: false}
            , {field: 'containerCode', title: '器具代码', width: 120, sort: false}
            , {field: 'containerTypeName', title: '器具类型', width: 150, sort: false}
            , {field: 'containerName', title: '器具名称', width: 150, sort: false}
            , {field: 'allNum', title: '系统保有量', width: 100, sort: false}
            , {field: 'actualNum', title: '实际盘点数', width: 100, sort: false}
            , {field: 'differentNum', title: '差异数', width: 100, sort: false}
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

  	//重置按钮
    layui.$('.resetButton').on('click', function(){
    	  $("#defferenceDealPanel input[name='inventoryId']").val("");
	      $("#defferenceDealPanel select[name='differentNumber']").val("");
	      form.render();
    });
    //列表数据重载
    var reloadTableData = function(currValue){
  	    layer.load(2); //加载中loading效果
        table.render({
            elem: '#LAY_TABLE'
            ,loading:true
            ,done: function(res, curr, count){
                layer.closeAll('loading'); //关闭加载中loading效果
            }
            ,url:  basePath + '/inventoryDetail/pagingInventoryDetailSum' //数据接口
            ,cols: [[
                {type:'numbers',title: '序号',width:60, sort: false}
                , {field: 'containerCode', title: '器具代码', width: 120, sort: false}
                , {field: 'containerTypeName', title: '器具类型', width: 150, sort: false}
                , {field: 'containerName', title: '器具名称', width: 150, sort: false}
                , {field: 'allNum', title: '系统保有量', width: 100, sort: false}
                , {field: 'actualNum', title: '实际盘点数', width: 100, sort: false}
                , {field: 'differentNum', title: '差异数', width: 100, sort: false}
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
    	var reloadParam = {
               where: {
            	   'inventoryId': $(".layui-search-form input[name='inventoryId']").val(),
                   'differentNumber': $(".layui-search-form select[name='differentNumber']").val(),
               }
        };
      	if(currValue){
      		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
      	}
    	table.reload('layuiReloadId', reloadParam);
    }
	//列表数据重载
    layui.$('.queryButton').on('click', function () {
        $("#orgInfo").html("");
        $("#timeInfo").html("");
        var param = {
            'inventoryId': $(".layui-search-form input[name='inventoryId']").val()
		}
        var InventoryInfo = "<tr>";
		if(param.inventoryId != ""){
            reloadTableData(1);  //重新从第 1 页开始，
            $.ajax({
                url: basePath + "/inventoryDetail/queryByInventoryId",
                type: "POST",
                dataType: "json",
                data: param,
                success: function (result) {
                    //layer.closeAll('loading'); //关闭加载中loading效果
                        if(result.status != 311) {
                            InventoryInfo = InventoryInfo + "<td class='mytable'>盘点单号:</td><td class='table1Column1Lable'>" + param.inventoryId + "</td>";
                            InventoryInfo = InventoryInfo + "</tr>"; //第一行结束
                            var orgInfo = "<tr>";
                            orgInfo = orgInfo + "<td class='mytable'>盘点仓库:</td><td class='table1Column1Lable'>"+result.bean.inventoryOrgName+"</td>";
                            orgInfo = orgInfo + "</tr>"; //第二行结束
                            $("#orgInfo").html(orgInfo);
                            var timeInfo = "<tr>";
                            timeInfo = timeInfo + "<td class='mytable'>盘点时间：</td><td class='table1Column1Lable'>"+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(result.bean.inventoryTime)+"</td>";
                            timeInfo = timeInfo + "</tr>"; //第三行结束
                            $("#timeInfo").html(timeInfo);
                        }
                        else{
                            InventoryInfo = InventoryInfo + "<td class='mytable'>盘点单号:</td><td class='redtable' >" + param.inventoryId + "  不存在！请核查！</td>";
                        }
                        $("#inventoryInfo").html(InventoryInfo);
                }
            })
		}
		else{
            InventoryInfo = InventoryInfo + "<td class='redtable' >"  + "  盘点编号不能为空！</td>";
            $("#inventoryInfo").html(InventoryInfo);
		}
    });
});
</script>
</html>