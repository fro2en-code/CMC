<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta charset="utf-8">
	<title>包装流转单</title>
	<style type="text/css">
		.mytable{
			margin-left: 30px;line-height: 30px;
		}
		.column1Lable{
			width: 100px;font-weight: 900;
		}
		.column1Value{
			width: 200px;
		}
		.table1Column1Lable{
			font-weight: 900;width: 100px;
		}
		.table1ColumnValue{
			width: 350px;
		}
	</style>
</head>
<body>
<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
<div class="layui-form layui-search-form myLabelWidth80">
	<div class="layui-form-item">
		<div class="layui-inline">
			<label class="layui-form-label" style="width:85px;">流转单单号</label>
			<div class="layui-input-inline">
				<input type="text" class="layui-input" name="orderCode">
			</div>
			<label class="layui-form-label" style="width:85px;">车牌号</label>
			<div class="layui-input-inline">
				<input type="text" class="layui-input" name="carNo">
			</div>
			<label class="layui-form-label" style="width:85px;">发货仓库</label>
			<div class="layui-input-inline">
				<select name="consignorOrgId" lay-search>
					<option value="">请选择</option>
				</select>
			</div>
			<label class="layui-form-label" style="width:85px;">收货仓库</label>
			<div class="layui-input-inline">
				<select name="targetOrgId" lay-search>
					<option value="">请选择</option>
				</select>
			</div>
		</div>
		<div class="layui-inline">
			<label class="layui-form-label" style="width:85px;">收货状态</label>
			<div class="layui-input-inline">
				<select name="isReceive" lay-search>
					<option value="">请选择</option>
					<option value="0">未收货</option>
					<option value="1">已部分收货</option>
					<option value="2">已全部收货</option>
				</select>
			</div>
			<label class="layui-form-label" style="width:85px;">出库类型</label>
			<div class="layui-input-inline">
				<select name="tradeTypeCode" lay-search>
					<option value="">请选择</option>
					<option value="3">流转出库</option>
					<option value="4">维修出库</option>
					<option value="5">租赁出库</option>
					<%--<option value="6">报废出库</option>--%>
					<option value="9">销售出库</option>
				</select>
			</div>
			<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
			<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
			<cmc:button buttonId="10"> <!-- 用户有该按钮权限才会显示以下代码 -->
				<button type="button" class="layui-btn layui-btn-sm addButton">创建流转单</button>
			</cmc:button>
			<cmc:button buttonId="86">
				<button type="button" class="layui-btn layui-btn-sm exportButton" id="exportButton">导出当前查询结果</button>
			</cmc:button>
		</div>
	</div>

	<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

	<script type="text/html" id="handleBar">
		<cmc:button buttonId="11">
			<a class="layui-btn layui-btn-xs" lay-event="print">打印</a>
		</cmc:button>
		<cmc:button buttonId="12">
			<a class="layui-btn layui-btn-xs" lay-event="orderDetil">查看明细</a>
		</cmc:button>
	</script>
	<%------------------------------------------------
                        中间内容结束
    ------------------------------------------------%>
</div>
</div>
</body>

<!-- 包装流转单器具明细弹出框 -->
<script type="text/html" id="ordePanel">
	<div class="layui-form ordePanellForm layerForm" action="">
		<table class="mytable">
			<tbody id="rderOrgInfo"> </tbody>
		</table>
		<table class="mytable">
			<tbody id="circulateOrderInfo"> </tbody>
		</table>
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
					<th>计划数量</th>
					<th>实发数量</th>
					<th>实收数量</th>
					<th>差异数</th>
				</tr>
				</thead>
				<tbody id="mySumTableBody"></tbody>
				<tfoot>
				<tr>
					<td colspan="9" id="myTablePaging"
						style="padding-top: 0px; padding-bottom: 0px;"></td>
				</tr>
				</tfoot>
			</table>
			<div id="receiveRemark" style="text-align:right;color:#ff5722;"></div>
			<div style="text-align:center;font-weight:900" id="containerDetailTableHeader">器具明细表</div>
			<table class="layui-hide" id="orderDetil" lay-filter="orderDetilFilter"></table>
		</div>
	</div>
</script>

<!-- 新增流转单弹出框 -->

<script type="text/html" id="addCirculateOrderPanel">
	<div class="layui-form layerForm addCirculateOrderPanelForm myLabelWidth125" action="">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">车牌号<span class="myRedColor">*</span></label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" name="carNo" placeholder="">
				</div>
				<label class="layui-form-label" style="width:150px;">司机姓名</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" name="driverName" placeholder="">
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">发货目的地仓库<span class="myRedColor">*</span></label>
				<div class="layui-input-inline">
					<select name="targetOrgId" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
				<label class="layui-form-label" style="width:150px;">司机联系方式</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" name="driverPhone" placeholder="">
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:150px;">出库类型<span class="myRedColor">*</span></label>
				<div class="layui-inline">
					<input type="radio" name="tradeTypeCode" value="3" title="流转出库"  lay-filter="radioFilter" checked="checked">
					<input type="radio" name="tradeTypeCode" value="4" title="维修出库"  lay-filter="radioFilter">
					<input type="radio" name="tradeTypeCode" value="5" title="租赁出库"  lay-filter="radioFilter">
					<input type="radio" name="tradeTypeCode" value="9" title="销售出库"  lay-filter="radioFilter">
				</div>
			</div>
		</div>
	</div>
</script>

<!-- 编辑特殊备注   -->
<script type="text/html" id="applianceRepairPanel">
	<div class="layui-form applianceRepairPanelForm layerForm myLabelWidth110" action="">
		<div id="epcIdInsertDiv" class="layui-form-item">
			<div class="layui-form-item">
			</div>
			<label class="layui-form-label">特殊备注: </label>
			<div class="layui-input-inline">
				<textarea name="maintainApplyBadReason" placeholder="请输入内容" class="layui-textarea" style="width: 320px; height: 350px;"></textarea>
			</div>
		</div>
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

		//获取所有仓库
        var orgList = [];
        $.ajax({
            type: 'POST',
            url: basePath + '/org/listAllOrg',
            dataType: 'json',
            success: function (data) {
                orgList = data.list;
                var orgOption = "<option value=''>直接选择或搜索选择</option>";
                for (var i=0;i<orgList.length;i++){
                    orgOption += "<option value='" + orgList[i].orgId + "'>" + orgList[i].orgName+ "</option>";
                }
                $(".layui-search-form select[name='targetOrgId']").html(orgOption);
                $(".layui-search-form select[name='consignorOrgId']").html(orgOption);
                form.render();
            }
        });
        
        //把作废的流转单那一行颜色字体灰掉
        var lineColorGray = function(data,val){
            if(null==val || undefined ==val)
			{
			    val='';
			}
        	if(data.isInvalid == 1){
        		return '<span style="color: #A5A5A5;">' + val + '</span>';
        	}
        	return val;
        }

        layer.load(2); //加载中loading效果
        //方法级渲染
        table.render({
            elem: '#LAY_TABLE'
            , loading: true
            ,done : function(res, curr, count) {
                layer.closeAll('loading'); //关闭加载中loading效果
            }
            , url: basePath + '/circulateOrder/pagingCirculateOrder' //数据接口
            , cols: [[
                {field: 'orderCode', title: '包装流转单号', width: 180, sort: false, templet: function (d) {
                	var result = d.orderCode;
                    if (d.isManualOrder && d.isManualOrder == '1') {
                    	result = lineColorGray(d, d.orderCode+'(手工单)' );
                    }
                    return result;
                }}
                , {field: 'carNo', title: '车牌号', width: 130, sort: false, templet: function (d) {
                	return lineColorGray(d, d.carNo);
                }}
                , {field: 'consignorOrgName', title: '发货仓库',  width: 200, sort: false, templet: function (d) {
                	return lineColorGray(d, d.consignorOrgName);
                }}
                , {field: 'targetOrgName', title: '收货仓库',  width: 200, sort: false, templet: function (d) {
                	return lineColorGray(d, d.targetOrgName);
                }}
                , {field: 'tradeTypeName', title: '出库类别',  width: 120, sort: false, templet: function (d) {
                	return lineColorGray(d, d.tradeTypeName);
                }}
                , {title: '操作', sort: false, align: 'center',  width: 360, templet:function (d) {
                	var result = '';
                	if(d.isInvalid == 1){ //如果单子作废，则什么都不用操作
                		return '<cmc:button buttonId="12"><a class="layui-btn layui-btn-xs" lay-event="orderDetil">查看明细 </a></cmc:button> ' + lineColorGray(d,'已作废') ;
                	}
                    result ='<cmc:button buttonId="11"><a class="layui-btn layui-btn-xs" lay-event="print">'+(d.printNumber>0?'再次打印':'打印并发货')+'</a></cmc:button>'
                     			+'<cmc:button buttonId="12"><a class="layui-btn layui-btn-xs" lay-event="orderDetil">查看明细</a></cmc:button>';
					if (d.isInvalid == 0 && d.printNumber < 1) { //编辑特殊备注
						result = result +'<cmc:button buttonId="89"><a class="layui-btn layui-btn-xs" lay-event="editSpecialComments">编辑特殊备注</a></cmc:button>';
					}
           			if (d.printNumber < 1) { //发货状态。一旦有打印，则视为已发货
           				result = result +'<cmc:button buttonId="82"><a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="invalidOrder">作废</a></cmc:button>';
                    }
                    return result;
				}}
                , {field: 'consignorAccount', title: '发货状态', width: 130, sort: false, templet: function (d) {
                    if (d.printNumber && d.printNumber > 0) { //发货状态。一旦有打印，则视为已发货
                    	return lineColorGray(d,"已发货");
                    }
                    return lineColorGray(d,"未发货");
                }}
                , {field: 'consignorRealName', title: '发货人和时间', width: 260, sort: false, templet: function (d) {
                    var result = '';
                	if (d.consignorRealName) {
                		result += '<span class="tableCellPre">'+ d.consignorRealName +'</span>';
                    }
                	if (d.consignorTime) {
                		result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.consignorTime) +'</span>';
                    }
                    return lineColorGray(d,result);
                }}
                , {field: 'isReceive', title: '收货状态', width: 130, sort: false, templet: function (d) {
                	var result = '';
                    if (d.isReceive) { //收货状态。是否已收货。0未收货，1已部分收货，2已全部收货
                        if(d.isReceive == '0'){
                        	result = "未收货";
                        }else if(d.isReceive == '1'){
                        	result = "已部分收货";
                        }else if(d.isReceive == '2'){
                        	result = "已全部收货";
                        }
                    }
                    return lineColorGray(d,result);
                }}
                , {field: 'remark', title: '收货备注', width: 130, sort: false, templet: function (d) {
                	return lineColorGray(d, d.remark);
                }}
                , {field: 'targetRealName', title: '收货人和时间', width: 260, sort: false, templet: function (d) {
                    var result = '';
                	if (d.targetRealName) {
                		result += '<span class="tableCellPre">'+ d.targetRealName +'</span>';
                    }
                	if (d.targetTime) {
                		result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.targetTime) +'</span>';
                    }
                    return lineColorGray(d,result);
                }}
                , {field: 'specialDescription', title: '特别描述',  width: 120, sort: false, templet: function (d) {
                	return lineColorGray(d, d.specialDescription);
                }}
                , {field: 'driverName', title: '司机姓名',  width: 120, sort: false, templet: function (d) {
                	return lineColorGray(d, d.driverName);
                }}
                , {field: 'driverPhone', title: '司机联系方式',  width: 120, sort: false, templet: function (d) {
                	return lineColorGray(d, d.driverPhone);
                }}
                , {field: 'carArriveTime', title: '车辆到达时间', width: 170, sort: false, templet: function (d) {
	                	if (d.carArriveTime) {
	                		var val = DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.carArriveTime);
	                		return lineColorGray(d, val);
	                    }
	                    return '';
                }}
                , {field: 'loadingEndTime', title: '装货完毕时间', width: 170, sort: false, templet: function (d) {
	                	if (d.loadingEndTime) {
	                		var val = DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.loadingEndTime);
	                		return lineColorGray(d, val);
	                    }
	                    return '';
            	}}
                , {field: 'printOrderTime', title: '首次打印流转单时间', width: 170, sort: false, templet: function (d) {
	                	if (d.printOrderTime) {
	                		var val = DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.printOrderTime);
	                		return lineColorGray(d, val);
	                    }
	                    return '';
	        	}}
                , {field: 'carLeaveTime', title: '车辆离开时间', width: 170, sort: false, templet: function (d) {
	                	if (d.carLeaveTime) {
	                		var val = DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.carLeaveTime);
	                		return lineColorGray(d, val);
	                    }
	                    return '';
	        	}}
                , {field: 'shipperName', title: '承运商', width: 170, sort: false, templet: function (d) {
                	return lineColorGray(d, d.shipperName);
                }}
                , {field: 'printNumber', title: '打印次数', width: 170, sort: false, templet: function (d) {
                	return lineColorGray(d, d.printNumber);
                }}
                , {field: 'creatTimeAndName', title: '操作日期', width: 200, height: 315
                    , templet: function (d) {
                        if (d.createTime) {
                        	var val = DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime);
	                		return lineColorGray(d, val);
                        }
                        return "";
                    }
                }
                , {field: 'createRealName', title: '操作人', width: 130, sort: false, templet: function (d) {
                	return lineColorGray(d, d.createRealName);
                }}
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
        //点击“查询”按钮
        layui.$('.queryButton').on('click', function () {
            reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
        });
        //重置按钮
        layui.$('.resetButton').on('click', function(){
            $(".layui-search-form input[name='orderCode']").val("");
            $(".layui-search-form input[name='carNo']").val("");
            $(".layui-search-form select[name='targetOrgId']").val("");
            $(".layui-search-form select[name='isReceive']").val("");
            $(".layui-search-form select[name='tradeTypeCode']").val("");
        });
        //列表数据重载
        var reloadTableData = function(currValue){
            layer.load(2); //加载中loading效果
            var reloadParam = {
                where: {
                    'orderCode': $(".layui-search-form input[name='orderCode']").val(),
                    'carNo': $(".layui-search-form input[name='carNo']").val(),
                    'consignorOrgId': $(".layui-search-form select[name='consignorOrgId']").val(),
                    'targetOrgId': $(".layui-search-form select[name='targetOrgId']").val(),
                    'isReceive': $(".layui-search-form select[name='isReceive']").val(),
                    'tradeTypeCode': $(".layui-search-form select[name='tradeTypeCode']").val()
                }
            };
            if(currValue){
                reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
            }
            table.reload('layuiReloadId', reloadParam);
        }

        //监听工具条
        table.on('tool(myLayFilter)', function (obj) {
            var data = obj.data;
            if (obj.event === 'print') {
                printFunction(data);
            } else if (obj.event === 'orderDetil') {
                orderDetil(data);//显示包装流转单器具明细
            } else if (obj.event === 'invalidOrder') {
            	invalidOrder(data);//作废
            } else if (obj.event === 'editSpecialComments') {
                editSpecialComments(data);//编辑特殊备注
            }
        });
        //作废
        var invalidOrder = function (data) {
            layer.confirm("确认要作废流转单["+data.orderCode+"]吗？作废后将不可恢复！", { title: "流转单作废确认" }, function (index) {
                var param = "orderCode=" + data.orderCode;
                layer.load(2); //加载中loading效果
                $.post(basePath + '/circulateOrder/invalidOrder', param, function (data) {
                	layer.closeAll('loading'); //关闭加载中loading效果
                    layer.msg(data.msg);
                    if (data.status == 200) {
                        layer.close(index);
                        reloadTableData(); //重新加载table数据
                    }
                });
            });
        }

        //编辑特殊备注
        var editSpecialComments = function (data) {
            layerEditName = layer.open({
                type: 1 //Page层类型
                , area: ['500px', '500px']
                , title: '编辑特殊备注'
                , shade: 0.6 //遮罩透明度
                , maxmin: true //允许全屏最小化
                , anim: -1 //0-6的动画形式，-1不开启
                , content: $("#applianceRepairPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                , cancel: function () {
                    //alert("关闭啦");
                }
                , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    $(".applianceRepairPanelForm textarea[name='maintainApplyBadReason']").val(data.specialDescription);
                    form.render();
                }
                , btn: ['保存', '关闭']
                , btn1: function (index) {
                    var param = {};
                    param['orderCode'] = data.orderCode;
                    param['specialDescription'] = $(".applianceRepairPanelForm textarea[name='maintainApplyBadReason']").val();
                    if (param['specialDescription'].length > 300) {
                        layer.msg("特殊备注最多300字符");
                        return;
                    }
                    layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/appCirculate/updateSpecialDescription",
                        type: "POST",
                        dataType: "json",
                        data: param,
                        success: function (result, textStatus, jqXHR) {
                            layer.closeAll('loading'); //关闭加载中loading效果
                            layer.msg(result.msg);
                            if (result.status == 200) {
                                layer.closeAll('page');   //成功后，关闭所有弹出框
                                reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
                            }
                        },error: function(index, upload){
                            layer.closeAll('loading'); //关闭加载中loading效果
                        }
                    });
                }
            });
        }

        //查看器具明细
        var orderDetil = function (data) {
            layerEditName = layer.open({
                type: 1 //Page层类型
                , area: ['1000px', '580px']
                , title: '包装流转单器具明细'
                , shade: 0.6 //遮罩透明度
                , maxmin: true //允许全屏最小化
                , anim: -1 //0-6的动画形式，-1不开启
                , content: $("#ordePanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                , cancel: function () {
                    //alert("关闭啦");
                }
                , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    var isReceiveStr = '';
                    if(data.isReceive == '0'){
                        isReceiveStr = '未收货';
                    }else if(data.isReceive == '1'){
                        isReceiveStr = '已部分收货';
                    }else if(data.isReceive == '2'){
                        isReceiveStr = '已全部收货';
                    }
                    var sendStatus = '未发货';
                    if(data.printNumber && data.printNumber > 0){
                    	sendStatus = '已发货';
                    }
                    //初始化发货仓库和收货仓库
                    var orgInfoThml = "<tr>";
                    orgInfoThml = orgInfoThml + "<td class='table1Column1Lable'>发货仓库:</td><td class='table1ColumnValue'>"+data.consignorOrgName+"</td>";
                    orgInfoThml = orgInfoThml + "<td class='table1Column1Lable'>收货仓库：</td><td class='table1ColumnValue'>"+data.targetOrgName+"</td>";
                    orgInfoThml = orgInfoThml + "</tr>"; //第一行结束
                    orgInfoThml = orgInfoThml + "<tr>";
                    orgInfoThml = orgInfoThml + "<td class='table1Column1Lable'>发货状态:</td><td class='table1ColumnValue'>"+sendStatus+"</td>";
                    orgInfoThml = orgInfoThml + "<td class='table1Column1Lable'>收货状态：</td><td class='table1ColumnValue'>"+isReceiveStr+"</td>";
                    orgInfoThml = orgInfoThml + "</tr>"; //第二行结束
                    orgInfoThml = orgInfoThml + "<tr>";
                    orgInfoThml = orgInfoThml + "<td class='table1Column1Lable'>发货人和时间:</td><td class='table1ColumnValue'>"+(data.consignorRealName?data.consignorRealName:"")+" " +
                    					(data.consignorTime?DateUtil.timeToYYYY_MM_dd_hh_mm_ss(data.consignorTime):"")+"</td>";
                    orgInfoThml = orgInfoThml + "<td class='table1Column1Lable'>收货人和时间：</td><td class='table1ColumnValue'>"+(data.targetRealName?data.targetRealName:"")+" " +
                    					(data.targetTime?DateUtil.timeToYYYY_MM_dd_hh_mm_ss(data.targetTime):"")+"</td>";
                    orgInfoThml = orgInfoThml + "</tr>"; //第三行结束
                    $("#rderOrgInfo").html(orgInfoThml);
                    //初始化包装流转单信息
                    var orderInfoThml = "<tr>";
                    orderInfoThml = orderInfoThml + "<td class='column1Lable'>包装流转单号:</td><td class='column1Value'>"+data.orderCode+((data.isManualOrder == "1")?"（手工单）":"")+"</td>";
                    orderInfoThml = orderInfoThml + "<td class='column1Lable'>车牌号:</td><td class='column1Value'>"+(data.carNo?data.carNo:"")+"</td>";
                    orderInfoThml = orderInfoThml + "<td class='column1Lable'>出库类别：</td><td>"+data.tradeTypeName+"</td>";
                    orderInfoThml = orderInfoThml + "</tr>"; //第一行结束
                    orderInfoThml = orderInfoThml + "<tr>";
                    orderInfoThml = orderInfoThml + "<td class='column1Lable'>总实发数量:</td><td class='column1Value' id='allSendNumberID'> </td>";
                    orderInfoThml = orderInfoThml + "<td class='column1Lable'>总实收数量:</td><td class='column1Value' id='allReceiveNumberID'> </td>";
                    orderInfoThml = orderInfoThml + "<td class='column1Lable'>总差异数：</td><td class='column1Value' id='allDifferentNumberID'> </td>";
                    orderInfoThml = orderInfoThml + "</tr>"; //第二行结束
                    $("#circulateOrderInfo").html(orderInfoThml);
                    form.render();
                    
                    if(data.isCirculateDetailReceive == '1'){
                    	$("#receiveRemark").html("提示：此单实收数量为手工输入");
                    }

                    //如果是手工流转单，则不必要渲染：器具明细表，只需要渲染：器具统计表
                    if(data.isManualOrder == '1'){
                          //渲染：器具统计表
		            	  layer.load(2); //加载中loading效果
		                  $.ajax({
				      		  url: basePath + "/circulateOrder/queryCirculateDetail",
				      		  type:"POST",
				      		  dataType: "json",
				      		  data: {"orderCode": data.orderCode,"page": 1,"limit": 100},
				      		  success: function(result, textStatus, jqXHR){
					              layer.closeAll('loading'); //关闭加载中loading效果
				      			  //layer.msg(result.msg);
					  			  if(result.status == 200){
			                            //渲染：器具统计表
			                            renderStatisticsList(result.bean,data);
						  		  }
						  	  },error: function(index, upload){
					            	layer.closeAll('loading'); //关闭加载中loading效果
					          }
			        	  });
		            	  $("#containerDetailTableHeader").hide();
                    	  return;
                    }

                    //器具明细表格渲染
                    table.render({
                        elem: '#orderDetil'
                        ,loading:true
                        ,done : function(res, curr, count) {
                            //渲染：器具统计表
                            renderStatisticsList(res.bean,data);
                        }
                        ,url:basePath + '/circulateOrder/queryCirculateDetail?orderCode='+data.orderCode
                        ,cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                        ,cols: [[
                            {field:'epcId', title: 'EPC编号', width:160, sort: false}
                            ,{field:'containerCode', title: '器具代码', width:100, sort: false} //width 支持：数字、百分比和不填写。你还可以通过 minWidth 参数局部定义当前单元格的最小宽度，layui 2.2.1 新增
                            ,{field:'receiveNumber', title: '收货数量', width:90, sort: false}
                            ,{field:'isReceive', title: '收货状态', width:90,templet: function(data){
                                var receiveNumberStr = "";
                                if(data.receiveNumber == 1){
                                    receiveNumberStr = "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#xe618;</i>";;
                                }else if(data.dealResult == 1){
                                    receiveNumberStr = "待处理";
                                }else{
                                    receiveNumberStr = "未收货";
                                }
                                return receiveNumberStr;
                            }}
                            ,{field: 'receiveRealName', title: '收货人', width:170, sort: false}
                            ,{field: 'receiveTime', title: '收货时间', width:160, sort: false,templet: function(d){
                                if(d.receiveTime){
                                	return  '<span>' +DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.receiveTime)+'</span>';
                                }
                                return "";
                            }}
                            ,{field:'receiveOrgName', title: '收货仓库名称', width:220, sort: false}
                        ]]
                        , id: 'layuiReloadId_orderDetil'
                        ,response: {
                            statusName: 'status' //数据状态的字段名称，默认：code
                            , statusCode: 200 //成功的状态码，默认：0
                            , msgName: 'msg' //状态信息的字段名称，默认：msg
                            , countName: 'total' //数据总数的字段名称，默认：count
                            , dataName: 'list' //数据列表的字段名称，默认：data
                        }
                    });
                }
                , btn: ['打印', '关闭']
                , btnAlign: 'c'
                , btn1: function (index) {
                    printFunction(data);
                }
            });
        }

        //渲染：器具统计表
        var renderStatisticsList = function(statisticsList,data){
            var tableHtml = "";
            console.log('statisticsList',statisticsList);
            for(var i=0;i<statisticsList.length;i++){
                var obj = statisticsList[i];
                tableHtml += "<tr><td>"+obj["containerCode"]+"</td>";
                tableHtml += "<td>"+obj["containerName"]+"</td>";
                tableHtml += "<td>"+obj["planNumber"]+"</td>";
                tableHtml += "<td>"+obj["sendNumber"]+"</td>";
                tableHtml += "<td>"+ obj["receiveNumber"] +"</td>";
                //tableHtml += "<td>"+obj["receiveNumber"]+"</td>";
                tableHtml += "<td>"+ obj["differentNumber"] +"</td></tr>";

                $("#allSendNumberID").html(obj["allSendNumber"]);
                $("#allReceiveNumberID").html(obj["allReceiveNumber"]);
                $("#allDifferentNumberID").html(obj["allDifferentNumber"]);
            }
            $("#mySumTableBody").html(tableHtml);
            form.render();
        }

        //监听工具条
        //删除
        table.on('tool(orderDetilFilter)', function (obj) {
            var data = obj.data;
            var param={};
            param['epcId']=data.epcId;
            param['orderCode']=data.orderCode;
            layer.load(2); //加载中loading效果
            $.ajax({
                url: basePath + "/circulateOrder/removeEpcFromCirculateOrder",
                type: "POST",
                dataType: "json",
                data: param,
                success: function (result, textStatus, jqXHR) {
                    layer.closeAll('loading'); //关闭加载中loading效果
                    layer.msg(result.msg);
                    if (result.status == 200) {
                        //不关闭弹出框，重新加载弹出框中table数据
                        table.reload('layuiReloadId_orderDetil', {
                            page: { curr: 1 },  //重新从第 1 页开始
                            where: {}
                        });
                    }
                },error: function(index, upload){
                    layer.closeAll('loading'); //关闭加载中loading效果
                }
            });
        });

        //打印
        var printFunction = function (data) {
            var param = {};
            if (!data.orderCode) {
                layer.msg("流转单号不能为空");
                return;
            }
            param['orderCode'] = data.orderCode;
            param['printNumber']=data.printNumber;
            layer.load(2); //加载中loading效果
            $.ajax({
                url: basePath + "/circulateOrder/printCirculateOrder",
                type: "POST",
                dataType: "json",
                data: param,
                success: function (result, textStatus, jqXHR) {
                    layer.closeAll('page');   //成功后，关闭所有弹出框
                    layer.closeAll('loading'); //关闭加载中loading效果
                    //刷新或者渲染某个页面单独表单的操作方式
                    if (result.status == 200) {
                        layer.msg("包装流转单["+param['orderCode']+"]打印成功！");
                        reloadTableData(); //重新加载table数据
                    }else{
                        layer.alert(result.msg);
                        if (result.status == 369) { //如果是发货成功但是打印失败的，也需要刷新列表数据
                            reloadTableData(); //重新加载table数据
                        }
                    }
                },error: function(index, upload){
                    layer.closeAll('loading'); //关闭加载中loading效果
                }
            });
        }

        //设置当前出库类型单选框的值
        var setRaioValue_TradeTypeCode = function(setValue){
            //出库类别单选框，设置值
            $(".layui-search-form input:radio[name='tradeTypeCode']").each(function () {
                if(this.value == setValue){
                    this.checked = true; //设置选中值
                }else{
                    this.checked = false;
                }
            })
            form.render();
        }

        //新增按钮点击
        layui.$('.addButton').on('click', function () {

            //监听“出库类型”单选框点击事件，动态更新checked属性
            form.on('radio(radioFilter)', function(data){
                var currentValue = data.value;
                setRaioValue_TradeTypeCode(data.value);
                form.render();
            });
            layerEditName = layer.open({
                type: 1 //Page层类型
                , area: ['800px', '450px']
                , title: '新增流转单'
                , shade: 0.6 //遮罩透明度
                , maxmin: true //允许全屏最小化
                , anim: -1 //0-6的动画形式，-1不开启
                , content: $("#addCirculateOrderPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                , cancel: function () {
                    //alert("关闭啦");
                }

                , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    var orgOption = "<option value=''>直接选择或搜索选择</option>";
                    for (var i=0;i<orgList.length;i++){
                        orgOption += "<option value='" + orgList[i].orgId + "'>" + orgList[i].orgName+ "</option>";
                    }
                    $(".addCirculateOrderPanelForm select[name='targetOrgId']").html(orgOption);
                    form.render();
                }
                , btn: ['保存']
                , btn1: function (index) {
                    var param = {};
                    param['carNo'] = $(".addCirculateOrderPanelForm input[name='carNo']").val();
                    param['driverName'] = $(".addCirculateOrderPanelForm input[name='driverName']").val();
                    param['driverPhone'] = $(".addCirculateOrderPanelForm input[name='driverPhone']").val();
                    param['targetOrgId'] = $(".addCirculateOrderPanelForm select[name='targetOrgId']").val();
                    $(".addCirculateOrderPanelForm input:radio[name='tradeTypeCode']").each(function () {
                        if(this.checked){
                            param['tradeTypeCode'] = this.value; //获取单选框的值：是否单独成托
                        }
                    })
                    layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/circulateOrder/createCirculateOrder",
                        type:"POST",
                        dataType: "json",
                        data: param,
                        success: function(result, textStatus, jqXHR){
                            layer.closeAll('loading'); //关闭加载中loading效果
                            reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
                            if(result.status == 200){
                                layer.closeAll('page');   //成功后，关闭所有弹出框
                                form.render();
                                layer.msg("操作成功");
                            }else{
                                layer.alert(result.msg);
                            }
                        },error: function(index, upload){
                            layer.closeAll('loading'); //关闭加载中loading效果
                        }
                    });

                }
            });
        });

        //导出
        layui.$('.exportButton').on('click', function () {
            var param ="?a=1";
            var carNo = $(".layui-search-form input[name='carNo']").val();
            var orderCode = $(".layui-search-form input[name='orderCode']").val();
            var targetOrgId = $(".layui-search-form select[name='targetOrgId']").val();
            var isReceive = $(".layui-search-form select[name='isReceive']").val();
            var tradeTypeCode = $(".layui-search-form select[name='tradeTypeCode']").val();
            console.log(targetOrgId);
            if (carNo){
                param+="&carNo="+carNo;
            }
            if (orderCode) {
                param += "&orderCode=" + orderCode;
            }
            if (targetOrgId) {
                param += "&targetOrgId=" + targetOrgId;
            }
            if (isReceive) {
                param += "&isReceive=" + isReceive;
            }
            if (tradeTypeCode) {
                param += "&tradeTypeCode=" + tradeTypeCode;
            }
            window.open(basePath + '/circulateOrder/expertToExcelCirculateOrder'+param,'targer','');

        });

    });

</script>
</html>