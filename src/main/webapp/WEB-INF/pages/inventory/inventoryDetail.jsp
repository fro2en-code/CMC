 <%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <title>盘点单详情</title>
    <style type="text/css">
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
					<label class="layui-form-label" style="width:120px;">盘点差异类型</label>
					<div class="layui-input-inline">
							<select name="isHaveDifferent" lay-verify="">
							    <option value="">请选择</option>
								<option value="1">无差异</option>
								<option value="2">有区域差异</option>
								<option value="3">未扫描到</option>
								<option value="4">扫描到新器具</option>
							</select>  
					</div>
				</div>
                <div class="layui-inline">
	                <button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
	                <button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				</div>
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
<!-- “确认丢失”弹出框 -->
<script type="text/html" id="lostPanel">
    <div class="layui-form layerForm lostPanelForm myLabelWidth100" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">丢失备注</label>
            <div class="layui-input-block">
					<input type="text" class="layui-input" name="lostRemark">
            </div>
        </div>
    </div>
</script>

<!-- “提交盘点数量”弹出框 -->
<script type="text/html" id="submitNumberPanel">
	<from class="layui-form layerForm myLabelWidth115 submitNumberPanelForm" action="">
		<div class="layui-form-item" style="margin-top: 20px;">
			<label class="layui-form-label">盘点编号:</label>
			<div class="layui-input-inline">
				<input type="text" name="inventoryId" class="layui-input layui-disabled" disabled>
			</div>
		</div>
		<div class="layui-form-item" style="margin-top: 20px;">
			<label class="layui-form-label">器具代码:</label>
			<div class="layui-input-inline">
				<input type="text" name="containerCode" class="layui-input layui-disabled" disabled>
			</div>
		</div>
		<div class="layui-form-item" style="margin-top: 20px;">
			<label class="layui-form-label">需盘点数量:</label>
			<div class="layui-input-inline">
				<input type="text" name="systemNumber" class="layui-input layui-disabled" disabled>
			</div>
		</div>
		<div class="layui-form-item" style="margin-top: 20px;">
			<label class="layui-form-label">盘点到数量:</label>
			<div class="layui-input-inline">
				<input type="text" name="inventoryNumber" placeholder="此项必填" autocomplete="off" class="layui-input" >
			</div>
		</div>
		<div class="layui-form-item" style="margin-top: 20px;display: none">
			<label class="layui-form-label">盘点明细ID:</label>
			<div class="layui-input-inline">
				<input type="text" name="inventoryDetailId" placeholder="此项必填" autocomplete="off" class="layui-input" >
			</div>
		</div>
	</from>
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

	  layer.load(2); //加载中loading效果
	  //方法级渲染
	  table.render({
	    elem: '#LAY_TABLE'
	    ,loading:true 
	    ,done: function(res, curr, count){
      		layer.closeAll('loading'); //关闭加载中loading效果
	    }
	    ,url:  basePath + '/inventoryDetail/pagingInventoryDetail' //数据接口
	    ,cols: [[
			{field: 'inventoryId', title: '盘点编号', width: 235, sort: false}
			, {field: 'epcId', title: 'EPC编号', width: 200, sort: false}
			, {field: 'containerCode', title: '器具代码', width: 120, sort: false}
			, {field: 'systemNumber', title: '需盘点数量', width: 120, sort: false}
			, {field: 'inventoryNumber', title: '盘点到数量', width: 120, sort: false}
			, {field: 'submitInventoryNumber', title: '提交盘点数量', width: 120, sort: false,templet: function(obj){
		  	  		var result = '';
		  	  		if(!obj.epcId && obj.inventoryState == 0){
				  	  	 <cmc:button buttonId="94"> 
				  	  		result = '<a class="layui-btn layui-btn-xs" lay-event="submitInventoryNumber">提交盘点数量</a>';
		  	        	 </cmc:button> 
		  	  		}
			  		return result;
			}}
			, {field: 'areaName', title: '盘点区域', width: 150, sort: false}
			, {field: 'isHaveDifferent', title: '差异类型', width: 160, sort: false,
			    templet: function (d) {
			    	//只要是没有EPC，都不显示差异类型
			    	if(!d.epcId){
				        return "";
			    	}
                    if (d.isHaveDifferent == 0) {
                        return "未知";
                    }
                    if (d.isHaveDifferent == 1) {
                        return "无差异";
                    }
                    if (d.isHaveDifferent == 2) {
			            return "区域差异";
			        } else if(d.isHaveDifferent == 3){
			            return "未扫描到";
			        }else if(d.isHaveDifferent == 4){
			            return "扫描到新器具";
			        }
			        return "";
			    }
			}
			, {field: 'oldAreaName', title: '旧区域', width: 130, sort: false}
			, {title: '差异处理', width:160, sort: false,templet: function(obj){
			    var result = '';
			    if(obj.isDeal == 0) {
                    if (obj.isHaveDifferent == 0) {
                        return "未知";
                    } else if (obj.isHaveDifferent == 1) {
                        return "无差异";
                    } else{
                    	//只有盘点状态为：“盘点完毕”的盘点单才可以对盘点差异进行处理
                    	//只要是没有EPC，都不显示差异处理
                    	if(obj.inventoryState == 1 && obj.epcId ){
                    		if (obj.isHaveDifferent == 2) {
                                <cmc:button buttonId="37"> /* 用户有该按钮权限才会显示以下代码 */
                                return '<a class="layui-btn layui-btn-xs" lay-event="modifyArea">修正区域</a>';
                                </cmc:button>
                            } else if (obj.isHaveDifferent == 3) {
                                <cmc:button buttonId="36"> /* 用户有该按钮权限才会显示以下代码 */
                                return '<a class="layui-btn layui-btn-xs" lay-event="confirmLoss">确认丢失</a>';
                                </cmc:button>
                            } else if (obj.isHaveDifferent == 4) {
                                <cmc:button buttonId="8"> /* 用户有该按钮权限才会显示以下代码 */
                                return '<a class="layui-btn layui-btn-xs" lay-event="newInstock">新增入库</a>';
                                </cmc:button>
                            }
                    	}
                    } 
                }
                else if(obj.isDeal == 1){
			        return "已处理";
				}
				return result;
			}}
			, {field: 'inventoryRealName', title: '盘点人', width: 110, sort: false}
			, {field: 'inventoryTime', title: '盘点时间', width: 180, sort: false,templet: function(obj){
			  	var result = "";
					if(obj.inventoryTime){
						result += DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.inventoryTime);
					}
				return result;
			}} 
			, {field: 'createRealName', title: '创建人', width: 110, sort: false}
			, {field: 'createTime', title: '创建时间', width: 180, sort: false,templet: function(obj){
			  	var result = "";
					if(obj.createTime){
						result += DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime);
					}
				return result;
			}} 
			, {field: 'inventoryDetailId', title: '盘点明细ID', width: 320, sort: false}
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
    });
    //列表数据重载
    var reloadTableData = function(currValue){
  	    layer.load(2); //加载中loading效果
    	var reloadParam = {
               where: {
            	   'inventoryId': $(".layui-search-form input[name='inventoryId']").val(),
                   'isHaveDifferent': $(".layui-search-form select[name='isHaveDifferent']").val()

               }
        };
      	if(currValue){
      		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
      	}
    	table.reload('layuiReloadId', reloadParam);
    }
	//列表数据重载
    layui.$('.queryButton').on('click', function () {
    	reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
    });
    //监听工具条
    table.on('tool(myLayFilter)', function (obj) {
        var data = obj.data;
        if (obj.event === 'confirmLoss') { //确认丢失
        	confirmLoss(data);
        } else if (obj.event === 'modifyArea') { //修改区域
        	modifyArea(data);
        } else if (obj.event === 'newInstock') { //入库
        	newInstock(data);
        } else if (obj.event === 'submitInventoryNumber') { //提交盘点数量
        	submitInventoryNumber(data);
        }
    });
    //确认丢失
    var confirmLoss = function (data) {
    	layerEditName = layer.open({
            type: 1 //Page层类型
            ,area: ['400px', '200px']
            ,title: '确认丢失器具【'+data['epcId']+'】'
            ,shade: 0.6 //遮罩透明度
            ,maxmin: true //允许全屏最小化
            ,anim: -1 //0-6的动画形式，-1不开启
            ,content: $("#lostPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            ,cancel: function(){
                //alert("关闭啦");
            }
            ,btn: ['保存']
            ,btn1: function(index){
            	var param = {};
            	param['inventoryId'] = data['inventoryId'];
            	param['epcId'] = data['epcId'];
            	param['lostRemark'] = $(".lostPanelForm input[name='lostRemark']").val();
                layer.load(2); //加载中loading效果
            	$.ajax({
                    url: basePath + "/inventoryDetail/confirmLoss",
                    type: "POST",
                    dataType: "json",
                    data: param,
                    success: function (result, textStatus, jqXHR) {
		            	layer.closeAll('loading'); //关闭加载中loading效果
                        layer.msg(result.msg);
                        if (result.status == 200) {
                            layer.closeAll();   //成功后，关闭所有弹出框
                            reloadTableData(); //重新加载table数据
                        }
                    },error: function(index, upload){
		            	layer.closeAll('loading'); //关闭加载中loading效果
			         }
                });
            }
        });
    }
    //提交盘点数量
    var submitInventoryNumber = function (data) {
    	layerEditName = layer.open({
            type: 1 //Page层类型
            ,area: ['400px', '400px']
            ,title: '提交盘点数量'
            ,shade: 0.6 //遮罩透明度
            ,maxmin: true //允许全屏最小化
            ,anim: -1 //0-6的动画形式，-1不开启
            ,content: $("#submitNumberPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            ,cancel: function(){
                //alert("关闭啦");
            }
            ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
          	    //设置弹出框form表单的值
          	    $(".submitNumberPanelForm input[name='inventoryId']").val(data.inventoryId);
          	    $(".submitNumberPanelForm input[name='containerCode']").val(data.containerCode);
          	    $(".submitNumberPanelForm input[name='systemNumber']").val(data.systemNumber);
          	    $(".submitNumberPanelForm input[name='inventoryNumber']").val(data.inventoryNumber);
          	    $(".submitNumberPanelForm input[name='inventoryDetailId']").val(data.inventoryDetailId);
                form.render();
            }
            ,btn: ['保存']
            ,btn1: function(index){
            	var param = {};
            	param['inventoryDetailId'] = $(".submitNumberPanelForm input[name='inventoryDetailId']").val();
            	param['inventoryNumber'] = $(".submitNumberPanelForm input[name='inventoryNumber']").val();
        	    var reg = /^[0-9]*$/;
                if(!(param['inventoryNumber'] && reg.test(param['inventoryNumber']) )){
                    layer.msg('盘点到数量不能为空且必须是正整数');
	        	    return;
                }
                console.log(param);
                layer.load(2); //加载中loading效果
            	$.ajax({
                    url: basePath + "/inventoryDetail/submitInventoryNumber",
                    type: "POST",
                    dataType: "json",
                    data: param,
                    success: function (result, textStatus, jqXHR) {
		            	layer.closeAll('loading'); //关闭加载中loading效果
                        layer.msg(result.msg);
                        if (result.status == 200) {
                            layer.closeAll();   //成功后，关闭所有弹出框
                            reloadTableData(); //重新加载table数据
                        }
                    },error: function(index, upload){
		            	layer.closeAll('loading'); //关闭加载中loading效果
			         }
                });
            }
        });
    }
    
    //修正区域
    var modifyArea = function (data) {
        layer.confirm("确认要修正器具["+data.epcId+"]的所在区域为["+data.areaName+"]吗？", { title: "修正区域确认" }, function (index) {
        	var param = {};
            param['inventoryId'] = data['inventoryId'];
            param['epcId'] = data['epcId'];
            param['inventoryDetailId'] = data['inventoryDetailId'];
        	$.ajax({
                url: basePath + "/inventoryDetail/modifyArea",
                type: "POST",
                dataType: "json",
                data: param,
                success: function (result, textStatus, jqXHR) {
                    layer.closeAll();   //成功后，关闭所有弹出框
                    layer.msg(result.msg);
                    if (result.status == 200) {
                        reloadTableData(); //重新加载table数据
                    }
                }
            });
        });
    }
    //新增入库
    var newInstock = function (data) {
        layer.confirm("确认要进行器具["+data.epcId+"]入库吗？", { title: "入库确认" }, function (index) {
        	var param = {};
        	param['inventoryId'] = data['inventoryId'];
        	param['epcId'] = data['epcId'];
        	$.ajax({
                url: basePath + "/inventoryDetail/add",
                type: "POST",
                dataType: "json",
                data: param,
                success: function (result, textStatus, jqXHR) {
                    layer.msg(result.msg);
                    if (result.status == 200) {
                        //layer.closeAll();   //成功后，关闭所有弹出框
                        reloadTableData(); //重新加载table数据
                    }
                }
            });
        });
    }
});
</script>
</html>