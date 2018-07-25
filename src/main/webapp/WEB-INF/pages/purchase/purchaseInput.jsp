<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>采购入库单</title>
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
				<label class="layui-form-label">开始日期</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" id="startDate" name="startDate">
				</div>
				<label class="layui-form-label">结束日期</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" id="endDate" name="endDate">
				</div>
				<label class="layui-form-label">入库单号</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" name="purchaseInOrgMainId" >
				</div>
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
           		<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
            </div>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

		<script type="text/html" id="handleBar">
			<cmc:button buttonId="26">
				<a class="layui-btn layui-btn-xs" lay-event="print">打印</a>
			</cmc:button>
			<cmc:button buttonId="27">
				<a class="layui-btn layui-btn-xs" lay-event="orderDetil">查看明细</a>
			</cmc:button>
		</script>

<%------------------------------------------------
                    中间内容结束
------------------------------------------------%>
	</div>
	</div>	
</body>
<script type="text/html" id="ordePanel">
	<div class="layui-form ordePanellForm" action="">
		<div class="layui-form-item">
			<label class="layui-form-label" style="width:100px;font-weight:bold;">入库单号:</label>
            <label class="layui-form-label" name="purchaseInOrgMainId" style="width:160px;text-align: left;"></label>
			<label class="layui-form-label" style="width:160px;font-weight:bold;">器具入库总数:</label>
			<label class="layui-form-label" name="inOrgNumber" style="width:160px;text-align: left;"></label>
		</div>
		<div class="layui-form-item">			
			<label class="layui-form-label" style="width:100px;font-weight:bold;">入库备注:</label>
			<label class="layui-form-label" name="inOrgRemark" style="width:160px;text-align: left;"></label>
		</div>
	</div>
<div style="padding: 0px 15px 0px 15px;">
	<div style="text-align:center;font-weight:900">器具统计表</div>
		<table class="layui-table">
			<colgroup>
				<col>
				<col >
				<col width="100">
			</colgroup>
			<thead>
				<tr>
					<th>器具代码</th>
					<th>器具名称</th>
					<th>数量</th>
				</tr>
			</thead>
			<tbody id="mySumTableBody"></tbody>
			<tfoot>
				<tr>
					<td colspan="9" id="myTablePaging" style="padding-top: 0px; padding-bottom: 0px;"></td>
				</tr>
			</tfoot>
		</table>
	<div style="text-align:center;font-weight:900">器具明细表</div>
	<table class="layui-hide" id="orderDetil" lay-filter="orderDetilFilter"></table>
</div>
</script>

<script type="text/javascript">
    layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element', 'form', 'layedit'], function () {
        var table = layui.table;
        var layer = layui.layer;
        var form = layui.form;
        var upload = layui.upload;
        var containerCoderOption;
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
            , url: basePath + '/purchase/pagingPurchaseInOrg' //数据接口
            , cols: [[
            	 {field: 'purchaseInOrgMainId',title: '入库单号 ', width:180, sort: false}
           	 	,{field: 'inOrgNumber',title: '器具入库总数 ',width:120, sort: false}
                ,{field: 'createOrgName',title: '创建仓库', sort: false,  width: 180}
                ,{field:'creatTimeAndName', title: '创建人与创建时间', width: 210,height: 315
                	, templet: function (d) {
                    console.log(d.createRealName);
                        if (d.createTime || d.createRealName) {
                            return '<span>' + d.createRealName + " "+DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime) +'</span>';
                            
                        }
                        return "";
                    }
                }	
                ,{title: '操作', sort: false,  width: 180, toolbar: '#handleBar'}
                , {field: 'printNumber', title: '打印次数', width: 100, sort: false}
                ,{field: 'inOrgRemark', title: '入库备注', width:220, sort: false}
           	  ]]
            , id: 'layuiReloadId'
            , page: true
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
        //列表数据重载
        var reloadTableData = function(currValue){
      	    layer.load(2); //加载中loading效果
        	var reloadParam = {
                   where: {
                	   'startDate': $(".layui-search-form").find("input[name='startDate']").val(),
                       'endDate': $(".layui-search-form").find("input[name='endDate']").val(),
                       'purchaseInOrgMainId': $(".layui-search-form input[name='purchaseInOrgMainId']").val()
                   }
            };
          	if(currValue){
          		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
          	}
        	table.reload('layuiReloadId', reloadParam);
        }
		//点击“查询”按钮
        layui.$('.queryButton').on('click', function () {
        	reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
        });
      //重置按钮
        layui.$('.resetButton').on('click', function(){
        	  $(".layui-search-form input[name='purchaseInOrgMainId']").val("");
        	  $(".layui-search-form input[name='startDate']").val("");
        	  $(".layui-search-form input[name='endDate']").val("");
        });

        //监听工具条
        table.on('tool(myLayFilter)', function (obj) {
            var data = obj.data;
            if (obj.event === 'print') {
                printFunction(data);
            } else if (obj.event === 'orderDetil') {
                orderDetil(data);//显示包装流转单器具明细
            }
        });
		//查看器具明细
        var orderDetil = function (data) {
            layerEditName = layer.open({
                type: 1 //Page层类型
                , area: ['1000px', '650px']
                , title: '采购入库单器具明细'
                , shade: 0.6 //遮罩透明度
                , maxmin: true //允许全屏最小化
                , anim: -1 //0-6的动画形式，-1不开启
                , content: $("#ordePanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                , cancel: function () {
                    //alert("关闭啦");
                }
                , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    $(".ordePanellForm label[name='purchaseInOrgMainId']").text(data.purchaseInOrgMainId);
                    $(".ordePanellForm label[name='inOrgNumber']").text(data.inOrgNumber);
                    $(".ordePanellForm label[name='inOrgRemark']").text(data.inOrgRemark);
                    form.render();
                    
					//器具明细表格渲染
                    table.render({
                        elem: '#orderDetil'
                        ,loading:true
                        ,done : function(res, curr, count) {
                        	form.render(); //渲染表格里的删除按钮
                        	var statisticsList = res.bean;
                        	var tableHtml = "";
                        	for(var i=0;i<statisticsList.length;i++){
                        		var obj = statisticsList[i];
                        		tableHtml += "<tr><td>"+obj["containerCode"]+"</td>";
                        		tableHtml += "<td>"+obj["containerName"]+"</td>";
                        		tableHtml += "<td>"+obj["purchaseCount"]+"</td></tr>";
                        	}
                        	$("#mySumTableBody").html(tableHtml);
                        	form.render();
                        }
                        ,url:basePath + '/purchase/queryInOrgDetail?purchaseInOrgMainId='+data.purchaseInOrgMainId
                        ,cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                        ,cols: [[
                            {field:'epcId', title: 'EPC编号（资产编码）', width:240, sort: true}
                            ,{field:'containerCode', title: '器具代码', width:240, sort: true} //width 支持：数字、百分比和不填写。你还可以通过 minWidth 参数局部定义当前单元格的最小宽度，layui 2.2.1 新增
                            ,{field:'containerName', title: '器具名称', width:240, sort: true}
                            ,{field: 'createTime', title: '创建时间', sort: false
                                ,templet: function(d){
                                    if(d.createTime==null){
                                        return "";
                                    }else{
                                        return  '<span>' +DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime)+'</span>';
                                    }
                                }
                             }
                            /* ,{field:'sign', title: '操作',toolbar: '#delBar'} */
                        ]]
                        , id: 'layuiReloadId_purchase'
                        ,response: {
                            statusName: 'status' //数据状态的字段名称，默认：code
                            , statusCode: 200 //成功的状态码，默认：0
                            , msgName: 'msg' //状态信息的字段名称，默认：msg
                            , countName: 'total' //数据总数的字段名称，默认：count
                            , dataName: 'data' //数据列表的字段名称，默认：data
                        }
                    });
                }
                , btn: ['下载资产编码excel', '打印采购入库单', '关闭']
                , btnAlign: 'c'

                , btn1: function (index) {
                    exportFunction(data);
                }
                , btn2: function (index) { //打印采购入库单
                	printFunction(data);
                }
            });
        }

        //打印
        var printFunction = function (data) {
			var param = {};
			if (!data.purchaseInOrgMainId) {
				layer.msg("入库单号不能为空");
				return;
			}
			param['printNumber'] = data.printNumber;
			param['purchaseInOrgMainId'] = data.purchaseInOrgMainId;
            layer.load(2); //加载中loading效果
			$.ajax({
				url: basePath + "/purchase/printInbound",
				type: "POST",
				dataType: "json",
				data: param,
				success: function (result, textStatus, jqXHR) {
	            	layer.closeAll('loading'); //关闭加载中loading效果
					//刷新或者渲染某个页面单独表单的操作方式
					if (result.status == 200) {
						layer.msg("采购入库单"+param['purchaseInOrgMainId']+"打印成功！");
						layer.closeAll('page');   //成功后，关闭所有弹出框
						reloadTableData(); //重新加载table数据
					}else{
						layer.msg(result.msg);
					}
				},error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	         	}
			});
        }

        //下载资产编码
        var exportFunction = function (data) {
            window.open(basePath + '/purchase/exportToExcelAssets?purchaseInOrgMainId='+data.purchaseInOrgMainId,'targer','');
        }
    });
</script>
</html>