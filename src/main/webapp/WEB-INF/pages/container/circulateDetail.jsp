<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>流转单收货详情</title>
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
				<label class="layui-form-label" style="width: 100px;">包装流转单号</label>
				<div class="layui-input-inline">
					<input type="text" name="orderCode" lay-verify="required"
						autocomplete="on" class="layui-input">
				</div>
			</div>
            <div class="layui-inline">
				<label class="layui-form-label" style="width: 100px">差异处理结果</label>
				<div class="layui-input-inline">
					<select name="differenceResult" lay-search>
						<option value="">请选择</option>
					</select>
				</div>
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="13"> <!-- 用户有该按钮权限才会显示以下代码 -->
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
<script type="text/html" id="editEpcPanel">
	<form class="layui-form editEpcPanelForm" action="">
		  <div class="layui-form-item">
          </div>
        <div class="layui-form-item">
            <label class="layui-form-label" style="width:150px";>EPC编号<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="newEpcId" lay-verify="required" placeholder="此项为必填" autocomplete="off" class="layui-input">
            </div>
        </div>
    </form>
</script>

<script type="text/html" id="circulateDetail">
	<form class="layui-form circulateDetailForm myLabelWidth125" action="">
		<div class="layui-form-item">
			<label class="layui-form-label" style="font-size: 17px ;font-weight:bold;">基础信息</label>
		</div>
		<div  class="layui-form-item">
			<label class="layui-form-label" >流转单号</label>
			<div class="layui-input-inline">
				<input type="text" name="orderCode" class="layui-input layui-bg-gray" disabled="disabled">
			</div>
			<label class="layui-form-label" >发货仓库</label>
			<div class="layui-input-inline">
				<input type="text" name="consignorOrgName" class="layui-input layui-bg-gray" disabled="disabled">
			</div>
			<label class="layui-form-label">目的地</label>
			<div class="layui-input-inline">
				<input type="text" name="targetOrgName" class="layui-input layui-bg-gray" disabled="disabled">
			</div>
		</div>
		<div  class="layui-form-item">
			<label class="layui-form-label">车牌号</label>
			<div class="layui-input-inline">
				<input type="text" name="carNo" class="layui-input layui-bg-gray" disabled="disabled">
			</div>
			<label class="layui-form-label">司机信息:</label>
			<div class="layui-input-inline">
				<input type="text" name="driver" class="layui-input layui-bg-gray" disabled="disabled">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label" style="font-size: 17px ;font-weight:bold";>收发货状态</label>
		</div>
		<div  class="layui-form-item">
			<label class="layui-form-label">发货状态</label>
			<div class="layui-input-inline">
				<input type="text" name="deliverState"  class="layui-input layui-bg-gray" disabled="disabled">
			</div>
			<label class="layui-form-label">发货人和时间</label>
			<div class="layui-input-inline">
				<input type="text" name="deliver" class="layui-input layui-bg-gray" disabled="disabled">
			</div>
			<label class="layui-form-label">打印次数</label>
			<div class="layui-input-inline">
				<input type="text" name="printNumber" class="layui-input layui-bg-gray" disabled="disabled" >
			</div>
		</div>
		<div  class="layui-form-item">
			<label class="layui-form-label">收货状态</label>
			<div class="layui-input-inline">
				<input type="text" name="receiveState" class="layui-input layui-bg-gray" disabled="disabled">
			</div>
			<label class="layui-form-label">收货人和时间:</label>
			<div class="layui-input-inline">
				<input type="text" name="receive" class="layui-input layui-bg-gray" disabled="disabled" >
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label" style="font-size: 17px ;font-weight:bold";>时间戳</label>
		</div>
		<div  class="layui-form-item">
			<label class="layui-form-label">车辆出发时间</label>
			<div class="layui-input-inline">
				<input type="text" name="carLeaveTime" class="layui-input layui-bg-gray" disabled="disabled" >
			</div>
			<label class="layui-form-label">车辆到达时间</label>
			<div class="layui-input-inline">
				<input type="text" name="carArriveTime" class="layui-input layui-bg-gray" disabled="disabled" >
			</div>
		</div>
		<div  class="layui-form-item">
			<label class="layui-form-label">流转单打印时间</label>
			<div class="layui-input-inline">
				<input type="text" name="printOrderTime" class="layui-input layui-bg-gray" disabled="disabled" >
			</div>
			<label class="layui-form-label">装货完成时间</label>
			<div class="layui-input-inline">
				<input type="text" name="loadingEndTime" class="layui-input layui-bg-gray" disabled="disabled" >
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
        //初始化差异处理结果select框
        $.ajax({
            type: 'POST',
            url: basePath + '/DifferencrResult/listDifferencrResult',
            dataType: 'json',
            success: function (data) {
            	if (data.status == 200 && data.list != null) {
                     var option="";
                    for (var i=0;i<data.list.length;i++){
                    	option += "<option value='" + data.list[i].differenceId + "'>" + data.list[i].differenceName+ "</option>";
                    }
                    $(".layui-search-form select[name='differenceResult']").append(option);
                    form.render();

                }
       }
    });

        layer.load(2); //加载中loading效果
        //方法级渲染
        table.render({
            elem: '#LAY_TABLE'
            ,loading:true
            ,url:  basePath + '/circulateDifferenceResult/pagingCirculateDetail' //数据接口
            ,done : function(res, curr, count) {
            	layer.closeAll('loading'); //关闭加载中loading效果
            	form.render();
        	}
            ,cols: [[
            	  {field: 'orderCode',title: '包装流转单号 ',width:190, sort: false, event: 'circulateDetail', style:'cursor: pointer;color: #0066FF;', templet: function (d) {
						var result = d.orderCode;
						if (d.isManualOrder && d.isManualOrder == '1') {
							result = lineColorGray(d, d.orderCode+'(手工单)' );
						}
						return result;
				}}
                ,{field: 'epcId', title: 'EPC编号', width:180, sort: false}
                ,{field: 'containerCode', title: '器具代码', width:130, sort: false}
                ,{field: 'containerTypeName', title: '器具类型', width:110, sort: false}
                ,{field: 'dealCoverageEpcId', title: '覆盖EPC编号', width:180, sort: false}
                ,{field: 'dealResult', title: '差异处理结果', width:230,  sort: false,templet: function(d){
                        if(d.isManualOrder!=1)
						{//1手工流转单 不需要按钮
							var result2 = '';
							<cmc:button buttonId="15"> /* 用户有该按钮权限才会显示以下代码 */
								result2 += "<a class='layui-btn layui-btn-xs' lay-event='epcDispose'>EPC覆盖</a>";
							</cmc:button>
							var result3 = '';
							<cmc:button buttonId="14"> /* 用户有该按钮权限才会显示以下代码 */
								result3 += "<a class='layui-btn layui-btn-xs' lay-event='inOrgDispose'>收货入库</a>";
							</cmc:button>
							var result4 = '';
							<cmc:button buttonId="16"> /* 用户有该按钮权限才会显示以下代码 */
								result4 += "<a class='layui-btn layui-btn-xs' lay-event='claimDispose'>索赔</a>";
							</cmc:button>
							if(d.dealResult == '1'){
								 return result2 + result3 + result4;
							}else if(d.dealResult == '2'){
								return "收货入库";
							}else if(d.dealResult == '3'){
								return "覆盖EPC";
							}else if(d.dealResult == '4'){
								return "索赔";
							}else if(d.dealResult == '5'){
								return "无差异";
							}
                        }
	                    return "";
                }}
                ,{field: 'receiveTime', title: '收货日期和收货人' , width:290, sort: false, templet: function (d) {
	            	    var result = "";
	                    if (d.receiveTime) {
	                    	result += '<span class="tableCellPre">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.receiveTime) + '</span>';
	                    }
	                    if (d.receiveRealName) {
	                    	result += '<span class="tableCellAfter">'+ d.receiveRealName + '</span>';
	                    }
	                    return result;
	                }
	            }
                ,{field: 'receiveOrgName', title: '收货仓库', width:180, sort: false}
                ,{field: 'sendNumber', title: '发货数量', width:120, sort: false}
                ,{field: 'receiveNumber', title: '实收数量', width:120, sort: false}
                ,{field: 'remark', title: '差异备注', width:120, sort: false}
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
       //初始化查询索赔类别 
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


        //监听工具条
        table.on('tool(myLayFilter)', function(obj) {
            var data = obj.data;
            if (obj.event == 'circulateDetail') { //监听到单元格点击事件
                var layerEditName = layer.open({
                    type: 1 //Page层类型
                    , area: ['1000px', '600px']
                    , title: '主单信息'
                    , shade: 0.6 //遮罩透明度
                    , maxmin: true //允许全屏最小化
                    , anim: -1 //0-6的动画形式，-1不开启
                    , content: $("#circulateDetail").html()
                    , cancel: function () {
                        //alert("关闭啦");
                    }
                    , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
						var orderCode = data.orderCode;
                        //console.log(orderCode);
                        $.ajax({
                            url: basePath + "/circulateOrder/queryCirculateOrderByOrderCode",
                            type:"POST",
                            dataType: "json",
                            data: {orderCode:orderCode},
                            success: function(data){
                                console.log(data.bean);
                                $(".circulateDetailForm input[name='orderCode']").val(data.bean.orderCode);
                                $(".circulateDetailForm input[name='consignorOrgName']").val(data.bean.consignorOrgName);
                                $(".circulateDetailForm input[name='targetOrgName']").val(data.bean.targetOrgName);
                                $(".circulateDetailForm input[name='carNo']").val(data.bean.carNo);
                                $(".circulateDetailForm input[name='driver']").val(data.bean.driverName +" " +data.bean.driverPhone);
                                $(".circulateDetailForm input[name='deliver']").val(data.bean.consignorRealName +"  "+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(data.bean.consignorTime));
                                $(".circulateDetailForm input[name='deliverState']").val('已发货');
                                $(".circulateDetailForm input[name='printNumber']").val(data.bean.printNumber);
                                var targetRealName = data.bean.targetRealName;
                                var targetTime = data.bean.targetTime;
                                if(targetRealName == null){
                                    targetRealName = '';
								}
								if(targetTime == null){
                                    $(".circulateDetailForm input[name='receive']").val(targetRealName +"  "+ '');
								}else {
                                    $(".circulateDetailForm input[name='receive']").val(targetRealName + "  " + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(targetTime));
                                }
								var receiveStateStr = '';
								var isReceive = data.bean.isReceive;
								//0未收货，1已部分收货，2已全部收货
								if(isReceive == '0'){
                                    receiveStateStr = '未收货';
								}else if(isReceive == '1'){
                                    receiveStateStr = '已部分收货';
								}else if(isReceive == '2'){
								    receiveStateStr = '已全部收货';
								}
                                $(".circulateDetailForm input[name='receiveState']").val(receiveStateStr);
								var carArriveTime = data.bean.carArriveTime;
								if(carArriveTime == null){
                                    $(".circulateDetailForm input[name='carArriveTime']").val('');
								}else {
                                    $(".circulateDetailForm input[name='carArriveTime']").val(DateUtil.timeToYYYY_MM_dd_hh_mm_ss(carArriveTime));
                                }
                                var loadingEndTime = data.bean.loadingEndTime;
                                if(loadingEndTime == null){
                                    $(".circulateDetailForm input[name='loadingEndTime']").val('');
                                }else {
                                    $(".circulateDetailForm input[name='loadingEndTime']").val(DateUtil.timeToYYYY_MM_dd_hh_mm_ss(loadingEndTime));
                                }
                                var carLeaveTime = data.bean.carLeaveTime;
                                if(carLeaveTime == null){
                                    $(".circulateDetailForm input[name='carLeaveTime']").val('');
                                }else {
                                    $(".circulateDetailForm input[name='carLeaveTime']").val(DateUtil.timeToYYYY_MM_dd_hh_mm_ss(carLeaveTime));
                                }
                                var printOrderTime = data.bean.printOrderTime;
                                if(printOrderTime == null){
                                    $(".circulateDetailForm input[name='printOrderTime']").val('');
                                }else {
                                    $(".circulateDetailForm input[name='printOrderTime']").val(DateUtil.timeToYYYY_MM_dd_hh_mm_ss(printOrderTime));
                                }
                                form.render();
                            }
                        });
                        form.render();
                    }
                    ,btn:['确定']
                    , btn1: function (index){
                        layer.closeAll();
					}
                });
            }

            if(obj.event === 'epcDispose'){
            	var layerEditName = layer.open({
                    type: 1 //Page层类型
                    ,area: ['400px', '250px']
                    ,title: '编辑EPC覆盖'
                    ,shade: 0.6 //遮罩透明度
                    ,maxmin: true //允许全屏最小化
                    ,anim: -1 //0-6的动画形式，-1不开启
                    ,content: $("#editEpcPanel").html()
                    ,cancel: function(){
                        //alert("关闭啦");
                    }
                    ,btn: ['确定覆盖']
                    ,btn1: function(index){
                    	var param = {};
                    	 param["circulateDetailId"] = data.circulateDetailId; 
                    	 param["epcId"] = data.epcId;
                    	 param["newEpcId"] = $(".editEpcPanelForm input[name='newEpcId']").val();
                    	 layer.load(2); //加载中loading效果
                    	$.ajax({
                            url: basePath + "/circulateDifferenceResult/epcDispose",
                            type:"POST",
                            dataType: "json",
                            data: param,
                            success: function(result, textStatus, jqXHR){
                            	layer.closeAll('loading'); //关闭加载中loading效果
                            	layer.msg(result.msg);
                            	if(result.status == 200){
                                    layer.closeAll('page');   //成功后，关闭所有弹出框
                                    reloadTableData(); //重新加载table数据
                                }
                            },error: function(index, upload){
    			            	layer.closeAll('loading'); //关闭加载中loading效果
    			         	}
                        });
                    }
                });
            }
            if(obj.event === 'inOrgDispose'){
                layer.confirm("确认要入库处理吗？", { title: "设置确认" }, function (index) {
                	var param = "?";
                		param += "&circulateDetailId=" + data.circulateDetailId + "&epcId=" + data.epcId;
                	layer.load(2); //加载中loading效果
                	$.post(basePath + '/circulateDifferenceResult/inOrgDispose', param, function (data) {
                		layer.closeAll('loading'); //关闭加载中loading效果
                        layer.msg(data.msg);
                        if (data.status == 200) {
                            layer.close(index);
                            reloadTableData(); //重新加载table数据
                        }
                    });
                });
            }
            if(obj.event === 'claimDispose'){
                layer.confirm("确认要索赔器具["+data.epcId+"]吗？", { title: "索赔确认" }, function (index) {
                	 var param = {};
	               	 param["circulateDetailId"] = data.circulateDetailId; 
	               	 param["epcId"] = data.epcId; 
	               	 layer.load(2); //加载中loading效果
	               	 $.ajax({
	                       url: basePath + "/circulateDifferenceResult/claimDispose",
	                       type:"POST",
	                       dataType: "json",
	                       data: param,
	                       success: function(result, textStatus, jqXHR){
	                       	layer.closeAll('loading'); //关闭加载中loading效果
	                       	layer.msg(result.msg);
	                       	if(result.status == 200){
	                               layer.closeAll('page');   //成功后，关闭所有弹出框
	                               reloadTableData(); //重新加载table数据
	                           }
	                       },error: function(index, upload){
				            	layer.closeAll('loading'); //关闭加载中loading效果
				         	}
	                   });
                });
            } 
        });
        
        //列表数据重载
        var reloadTableData = function(currValue){
      	    layer.load(2); //加载中loading效果
        	var reloadParam = {
                   where: {
                	    'differenceId':$(".layui-search-form select[name='differenceResult']").val(),
                   		'orderCode':$(".layui-search-form").find("input[name='orderCode']").val()
                   }
            };
          	if(currValue){
          		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
          	}
        	table.reload('layuiReloadId', reloadParam);
        }
      //重置按钮
        layui.$('.resetButton').on('click', function(){
        	  $(".layui-search-form input[name='orderCode']").val("");
        	  $(".layui-search-form select[name='differenceResult']").val("");
        	  form.render();
        });
      //查询按钮
        layui.$('.queryButton').on('click', function(){
        	reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
        });
      //导出当前查询结果按钮
        layui.$('.exportButton').on('click', function () {
        	var param = "?a=1";
            var orderCode = $(".layui-search-form input[name='orderCode']").val(); //包装流转单号
	           if(orderCode){
	        	   param += "&orderCode="+orderCode;
	           }
	           var differenceId = $(".layui-search-form select[name='differenceResult']").val();//流转单差异处理状态
	           if(differenceId){
	        	   param += "&differenceId="+differenceId;
	           }
           	 window.open(basePath + '/circulateDifferenceResult/expertToExcel'+param,'target','');
        });

    //把作废的流转单那一行颜色字体灰掉
    var lineColorGray = function(data,val){
        if(data.isInvalid == 1){
            return '<span style="color: #A5A5A5;">' + val + '</span>';
        }
        return val;
    }
});
</script>
</html>