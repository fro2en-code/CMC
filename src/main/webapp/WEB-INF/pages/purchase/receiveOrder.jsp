<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <title>收货</title>
    <style type="text/css">
    </style>
  </head>
<body>
    <%------------------------------------------------
        					右边内容开始
        ------------------------------------------------%>
<div class="demoTable layui-form layui-search-form" id="purchaseReceivePanel">
	<div class="layui-form-item">
		<label class="layui-form-label" style="width:120px;">包装流转单号</label>
		<div class="layui-inline">
			<input type="text" class="layui-input" name="orderCodeSearch"  placeholder="" style="width: 280px;">
		</div>
		<div class="layui-inline">
			<button class="layui-btn layui-btn-sm queryButton" >查询</button>
			<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
		</div>
	</div>
</div>

	<!-- 包装流转单列表 -->
	<table class="layui-hide" id="LAY_TABLE_ORDER" lay-filter="myLayFilterOrder"></table>

<div class="demoTable layui-form layui-search-form" id="purchaseReceivePanel">
	<div class="layui-form-item">
		<label class="layui-form-label" style="width:120px;font-weight: bold;">包装流转单号：</label>
		<div class="layui-inline">
			<input type="text" class="layui-input layui-disabled" disabled="disabled" name="orderCode"  placeholder="" style="width: 280px;">
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label" style="width:120px;font-weight: bold;">车牌号：</label>
		<div class="layui-inline">
			<input type="text" class="layui-input layui-disabled" disabled="disabled" name="carNo"  placeholder="" style="width: 280px;">
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label" style="width:120px;font-weight: bold;">配送目的地：</label>
		<div class="layui-inline">
			<input type="text" class="layui-input layui-disabled" disabled="disabled" name="targetOrgName"  placeholder="" style="width: 280px;">
		</div>
	</div>
</div>

	<!-- 器具统计 -->
	<div class="demoTable layui-form layui-search-form blockSum" style="display: none">
		<div class="layui-form-item">
			<div class="layui-input-block" style="width:600px;text-align: center;">
				<b>器具统计表</b>
			</div>
		</div>
	</div>
	<table class="layui-hide" id="LAY_TABLE_SUM" lay-filter="myLayFilterSum"></table>

	<div class="demoTable layui-form layui-search-form blockSum" style="display: none">
		<div class="layui-form-item">
			<div class="layui-input-block" style="width:600px;text-align: center;">
				<cmc:button buttonId="28"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm confirmButton" >收货入库</button>
				</cmc:button>
			</div>
		</div>
	</div>

        <%------------------------------------------------
        					右边内容结束
        ------------------------------------------------%>
</div></div>
</body>
<script type="text/javascript">
layui.use([ 'layer', 'table', 'element','form'], function(){
        var table = layui.table;
        var layer = layui.layer;
        var form = layui.form;
        var element = layui.element;
        initLeftmenu(element); //初始化左边菜单导航栏

        layer.load(2); //加载中loading效果
    	  //方法级渲染
    	table.render({
    	    elem: '#LAY_TABLE_ORDER'
    	    ,loading:true
    	    ,done: function(res, curr, count){
    	    	layer.closeAll('loading'); //关闭加载中loading效果
    	    	form.render();
    	    }
    	    ,url:  basePath + '/circulateOrder/pagingCirculateOrderReceiveCurrentOrg' //包装流转单列表
    	    ,cols: [[
    	                {field: 'orderCode', title: '包装流转单号', width: 180, sort: false, templet: function (d) {
    	                	if(d.isManualOrder == '1'){
    	                		return d.orderCode+'(手工单)';
    	                	}
	                		return d.orderCode;
	                    }} //, fixed: 'left'
    	                , {field: 'carNo', title: '车牌号', width: 130, sort: false}
    	                , {field: 'consignorOrgName', title: '发货仓库',  width: 150, sort: false}
    	                , {field: 'targetOrgName', title: '收货仓库',  width: 180, sort: false}
    	                , {field: 'tradeTypeName', title: '出库类别',  width: 100, sort: false}
    	                , {title: '操作', sort: false, align: 'center',  width: 90, templet: function (d) {
    	                		return '<a class="layui-btn layui-btn-xs" lay-event="showOrder">查看</a>';
	                    }}
    	                , {field: 'creatTimeAndName', title: '操作日期', width: 180, height: 315
    	                    , templet: function (d) {
    	                        if (d.createTime) {
    	                            return '<span>' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime) + '</span>';
    	                        }
    	                        return "";
    	                    }
    	                }
    	                , {field: 'isReceive', title: '收货状态', width: 140, templet: function (d) {
    	                        if (d.isReceive == 0) {
    	                            return '未收货';
    	                        }else if (d.isReceive == 1) {
    	                            return '已部分收货';
    	                        }else if (d.isReceive == 2) {
    	                            return '已全部收货';
    	                        }else{
    	                        	return ""+d.isReceive;
    	                        }
    	                        return ""+d.isReceive;
    	                    }
    	                }
    	                , {field: 'printNumber', title: '发货状态', width: 140, templet: function (d) {
    	                        if (d.printNumber && d.printNumber > 0) {
    	                            return '已发货';
    	                        }
    	                        return '未发货';
    	                    }
    	                }
    	                , {field: 'createRealName', title: '操作人', width: 130, sort: false}
    	                , {field: 'printNumber', title: '打印次数', width: 130, sort: false}
    	    ]]
    	    ,id: 'layuiReloadId_circulate'
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
    		        ,limits: [3, 10, 50] //支持每页数据条数选择
    		        ,groups: 10 //显示 10 个连续页码
    		        ,jump: function(obj){
    		        }
    		 }  //开启分页
    	  });

		  //点击“查询”按钮，加载包装流转单列表
        layui.$('.queryButton').on('click', function () {
	          	layer.load(2); //加载中loading效果
	            table.reload('layuiReloadId_circulate', {  //查询包装流转单列表
	                page: { curr: 1 },  //重新从第 1 页开始
	                where: {
	                    'orderCode': $(".layui-search-form input[name='orderCodeSearch']").val()
	                }
	            });
        });

        //监听包装流转单列表里的“查看”按钮
        table.on('tool(myLayFilterOrder)', function(obj){
            var data = obj.data;
            if(obj.event === 'showOrder'){ //"查看"按钮点击
          	  	showCirculateOrder(data);
            }
        });

        //包装流转单列表里的“查看”按钮点击
        var showCirculateOrder = function(data){
        	var param = {};
        	param['orderCode'] = data.orderCode;
        	if(!param['orderCode']){
        		layer.msg("包装流转单号不能为空");
        		return;
        	}
        	layer.load(2); //加载中loading效果
        	$.ajax({
                url: basePath + "/circulateOrderReceive/queryCirculateOrder",
                type:"POST",
                dataType: "json",
                data: param,
                success: function(result, textStatus, jqXHR){
                	layer.closeAll('loading'); //关闭加载中loading效果
                    if(result.status == 200){
                    	var circulateOrder = result.bean;
                		var epcSumList = result.list;
                		$(".layui-search-form input[name='orderCode']").val(circulateOrder.orderCode);
                		$(".layui-search-form input[name='carNo']").val(circulateOrder.carNo);
                		$(".layui-search-form input[name='targetOrgName']").val(circulateOrder.targetOrgName);
                		initSumTableData(epcSumList, data.isManualOrder);
                    }else{
                    	layer.msg(result.msg);
                    }
                },error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	         	}
            });
        }

    	var tableSumIns;
        var initCirDetails = '';
        var manualOrder;
        //初始化器具统计表格
        var initSumTableData = function(epcSumList, isManualOrder){
        	//方法级渲染
			manualOrder = isManualOrder;
            tableSumIns = table.render({
                elem: '#LAY_TABLE_SUM'
                ,width: 780
                ,data: epcSumList
                ,cols: [[
                    {type: 'numbers', title: '序号', width: 60, sort: false}
                    , {field: 'containerCode', title: '器具代码', width: 130, sort: false}
                    , {field: 'containerName', title: '器具名称', sort: false}
                    , {field: 'planNumber', title: '计划数量', width: 100, sort: false}
                    , {field: 'sendNumber', title: '实发数量', width: 100, sort: false}
                    , {field: 'receiveNumber', title: '实收数量', width: 100, sort: false, edit: 'text'}
                ]],
                done: function(res, curr, count){

                    $(".blockSum").show();
                    // 获得器材表数据
					if (!$.isEmptyObject(epcSumList)) {
                        initCirDetails = JSON.stringify(epcSumList);
					}
                }
            });

            table.on('edit(myLayFilterSum)', function (obj) {
                //obj.data.sendNumber  发货数量
                //obj.data.receiveNumber 收货数量
                var ex = /^\d+$/;
                if (!ex.test(obj.value)) {
                    layer.msg("请输入正整数！");
                    $(this).val(0);
                    var dataArray = tableSumIns.config.data;
                    for (var i = 0; i < dataArray.length; i++)
                    {
                        if(obj.data.containerCode == dataArray[i]["containerCode"])
                        {
                            dataArray[i]["receiveNumber"] = 0;
                        }
                    }
                    return;
                }
                if(obj.data.receiveNumber>obj.data.sendNumber)
				{
                    layer.msg("收货数量不能大于发货数量！");
                    var dataArray = tableSumIns.config.data;
                    var temp = "";
                    $(this).val(obj.data.sendNumber);
                    for (var i = 0; i < dataArray.length; i++) {
                        if(obj.data.containerCode == dataArray[i]["containerCode"])
						{
                            dataArray[i]["receiveNumber"] = obj.data.sendNumber;
						}
                    }
                    return;
				}
            });

        }
      //重置按钮
        layui.$('.resetButton').on('click', function(){
        	  $("#purchaseReceivePanel input[name='orderCodeSearch']").val("");

        });
        //点击按钮：收货入库
        layui.$('.layui-search-form .confirmButton').on('click', function(){
        	var param = {};
        	param['orderCode'] = $(".layui-search-form input[name='orderCode']").val();
        	if(!param['orderCode']){
        		layer.msg("包装流转单单号不能为空");
        		return;
        	}
        	layer.confirm("确认要进行收货入库吗？", { title: "收货确认" }, function (index) {
            	// 赋值器材表数据
                var url = '';
            	if (initCirDetails != '') {
            		var dataArray = tableSumIns.config.data;
            		var newDataArray = [];
            		var isEqual = true;
            		for (var i = 0; i < dataArray.length; i++) {
            		    //console.log("receiveNumber["+i+"]"+dataArray[i]["receiveNumber"]);
						if(isEqual && dataArray[i]["sendNumber"] != dataArray[i]["receiveNumber"]) {
                            isEqual = false;
						}
            			newDataArray[i] = {"containerCode":dataArray[i]["containerCode"],
							"planNumber":dataArray[i]["planNumber"],
							"sendNumber":dataArray[i]["sendNumber"],
							"receiveNumber":dataArray[i]["receiveNumber"],
							"orderCode":dataArray[i]["orderCode"]};
    				}
                    var curCirDetails = JSON.stringify(newDataArray);
                    // console.log(initCirDetails);
                    // console.log(curCirDetails);
                    // 对比赋值
            	    if (initCirDetails != curCirDetails) {
                        param['circulateDetailsStr'] = curCirDetails;
    				}
                    if (manualOrder == 1) {
                        url = "/circulateOrderReceive/confirmInOrg";
                    } else {
                        url = isEqual ? "/circulateOrderReceive/confirmInOrg":"/circulateOrderReceive/inOrgWebOrder";
                    }
    			}
           	    layer.load(2); //加载中loading效果
            	$.ajax({
                    url: basePath + url,
                    type:"POST",
                    dataType: "json",
                    data: param,
                    success: function(result, textStatus, jqXHR){
                    	layer.closeAll('loading'); //关闭加载中loading效果
                        if(result.status == 200){
                        	layer.msg(result.msg);
                        	$('.queryButton').click();
                        }else{
                        	layer.alert(result.msg);
                        }
                    },error: function(index, upload){
    	            	layer.closeAll('loading'); //关闭加载中loading效果
    	         	}
                });
        	});
        });

});
</script>
</html>