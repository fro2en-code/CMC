<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>器具维修</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="layui-form layui-search-form" id="containerMaintainPanel">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">EPC编号</label>
				<div class="layui-input-inline">
					<input type="text" name="epcId" lay-verify="required" autocomplete="on" class="layui-input">
				</div>
				<label class="layui-form-label" style="width: 80px">印刷编号</label>
				<div class="layui-input-inline">
					<input type="text" name="printCode" id="printCode" lay-verify="required" autocomplete="on" class="layui-input">
				</div>
				<label class="layui-form-label">维修状态</label>
				<div class="layui-input-inline">
					<select name="maintainState" lay-filter="maintainStateFilter" lay-search>
						<option value="">请选择</option>
					</select>
				</div>
				<div class="layui-input-inline">
					<input type="checkbox" name="outModeCheckBox" id="checkBoxId" title="过时维修" lay-filter="checkBoxLayFilter" lay-skin="primary" >
				</div>
			</div>
			<div class="layui-inline">
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
					<!-- 用户有该按钮权限才会显示以下代码 -->
					<%-- <cmc:button buttonId="52"> 
						<button class="layui-btn layui-btn-sm addButton" data-type="">器具报修与报废申请</button>
					</cmc:button> --%>
					<cmc:button buttonId="17"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm addButton" data-type="">器具报修</button>
					</cmc:button>
					<cmc:button buttonId="18"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button class="layui-btn layui-btn-sm exportButton" data-type="reload">导出当前查询结果</button>
					</cmc:button>
				</div>
			</div>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
	</div>
</body>
<%------------------------------------------------
                    中间内容结束
------------------------------------------------%>
<!--报修与报废申请弹出框-->
<script type="text/html" id="editContainerPanel">
	<div class="layui-form editContainerPanelForm layerForm myLabelWidth110" action="">
		<div id="epcIdInsertDiv" class="layui-form-item">
			<label class="layui-form-label">EPC编号<span class="myRedColor">*</span></label>
			<div class="layui-input-inline">
				<input type="text" name="epcId" placeholder="请输入" autocomplete="off" class="layui-input">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">维修级别<span class="myRedColor">*</span></label>
			<div class="layui-input-block maintainLevelList">
			</div>
		</div>
		<div class="appendMaintainLevelList">
		</div>
	</div>
</script>

<!-- 器具报修   -->
<script type="text/html" id="applianceRepairPanel">
	<div class="layui-form applianceRepairPanelForm layerForm myLabelWidth110" action="">
		<div id="epcIdInsertDiv" class="layui-form-item">
			<label class="layui-form-label">EPC编号<span class="myRedColor">*</span></label>
			<div class="layui-input-inline">
				<input type="text" name="epcId" placeholder="请输入" autocomplete="off" class="layui-input">
			</div>
			<div class="layui-form-item">
        	</div>
			<label class="layui-form-label">不良原因<span class="myRedColor">*</span>:</label>
            <div class="layui-input-inline">
				<textarea name="maintainApplyBadReason" placeholder="请输入内容" class="layui-textarea"></textarea>
            </div>
		</div>
	</div>
</script>

<!-- 维修鉴定 -->
<script type="text/html" id="maintainAppraisalIdPanel">
	<div class="layui-form maintainAppraisalIdPanelForm layerForm myLabelWidth110" action="">
		<div class="layui-form-item">
			<label class="layui-form-label" style="width:150px">EPC编号:<span class="myRedColor">*</span></label>
			<div class="layui-input-inline">
				<input type="text" name="epcId" class="layui-input layui-disabled" disabled="disabled">
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label" style="width:150px">维修级别鉴定:<span class="myRedColor">*</span></label>
		</div>
		<div class="layui-form-item">
			<div class="layui-input-block appendMaintainLevelLists">
			</div>
		</div>
	</div>
</script>

<!-- <input type="text" name="measure" autocomplete="off" class="layui-input" style="width:500px">
<input type="text" name="cause" autocomplete="off" class="layui-input" style="width:500px">
-->
<!-- 维修完成-->
<script type="text/html" id="mainFinishIdPanel">
<div class="layui-form mainFinishIdPanelForm layerForm myLabelWidth110" action="">
		<div id="epcIdInsertDiv" class="layui-form-item">
			<label class="layui-form-label">EPC编号:<span class="myRedColor">*</span></label>
			<div class="layui-input-inline">
				<input type="text" name="epcId" lay-verify="required" autocomplete="off" class="layui-input layui-disabled" disabled="disabled">
			</div>
			<div class="layui-form-item">
        	</div>
			<label class="layui-form-label">维修措施:</label>
            <div class="layui-input-inline">
				<textarea name="maintainFinishSolution" class="layui-textarea"></textarea>
            </div>
			<div class="layui-form-item">
        	</div>
			<label class="layui-form-label">不良原因:</label>
            <div class="layui-input-inline">
				<textarea name="maintainFinishBadReason" class="layui-textarea"></textarea>
            </div>
		</div>
	</div>
</script>

<script type="text/html" id="maintainScrapPanel">
	<div class="layerForm layui-form maintainScrapPanelForm myLabelWidth115" action="">
		<div class="layui-form-item">
			<label class="layui-form-label">报废方式<span class="myRedColor">*</span></label>
			<div class="layui-input-inline">
				<select name="scrapWayName" lay-filter="scrapWayNameFilter" lay-search>
					<option value="">请选择</option>
				</select>
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

        //初始化维修状态select框
        $.ajax({
            type: 'POST',
            url: basePath + '/maintain/getAllMaintainStatus',
            dataType: 'json',
            success: function (data) {
                var states = data.list;
                var option="";
                for (var i=0;i<states.length;i++){
                    option += "<option value='" + states[i].code + "'>" + states[i].value + "</option>";
                }
                layui.$(".layui-search-form select[name='maintainState']").append(option);
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
            	form.render();
        	}
            , url: basePath + '/maintain/pagingMaintain' //数据接口
            , cols: [[
                  {field: 'epcId', title: 'EPC编号', width: 180, sort: false}
                , {field: 'printCode', title: '印刷编号', width: 150 , sort: false}
                , {field: 'containerTypeName', title: '器具类型', width: 100 , sort: false}
                , {field: 'maintainState', title: '维修状态', width: 100 , sort: false,
                    templet: function (d) {
                    	if(d.maintainState=='1'){
                    		return "在库维修";
                    	}else if(d.maintainState=='2'){
                    		return "出库维修";
                    	}else if(d.maintainState=='3'){
                    		return "维修完毕";
                    	}
                    	return "";
                    }
				}
                , {field: 'maintainLevel', title: '维修级别', width: 130, sort: false, templet: function (obj) {
	    	  	  		var result = ''; 
	    	  	  		if(obj.maintainLevel){
	    	  	  			result = obj.maintainLevel;
	    	  	  		} else if (!obj.maintainLevel && !obj.maintainFinishTime) { //如果维修级别为空或者尚未维修完毕，才需要维修鉴定
		    				<cmc:button buttonId="19">
		    				result += '<a class="layui-btn layui-btn-xs" lay-event="maintainAppraisal">维修鉴定</a>';
		    				</cmc:button>
	                    } 
	    	        	return result;
	            }}
                , {field: 'maintainApplyBadReason', title: '报修不良原因', width: 120 , sort: false}
               /*  , {field: '', title: '维修鉴定', width: 130, sort: false, templet: function (obj) {
	    	  	  		var result = ''; 
	                    if (!obj.maintainLevel && !obj.maintainFinishTime) { //如果维修级别为空或者尚未维修完毕，才需要维修鉴定
		    				result += '<a class="layui-btn layui-btn-xs" lay-event="maintainAppraisal">维修鉴定</a>';
	                    } 
	    	        	return result;
                }} */
                , {field: 'maintainFinishTime', title: '操作人与维修完成时间', width: 180, sort: false
                	, templet: function (obj) {
        	  	  		var result = '';
                        <cmc:button buttonId="20">
        	  	  		result += "<a class='layui-btn layui-btn-xs' lay-event='maintainFinish'>维修完成</a>";
                        </cmc:button>
                        if (obj.maintainState == '3') { //如果是维修完成，则显示维修完毕操作时间与操作人
            		  		if(obj.maintainApplyRealName && obj.maintainApplyTime){
            		  			return '<span class="tableCellPre">'+ obj.maintainFinishRealName + '</span>'+'<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.maintainFinishTime) +'</span>';
            		  		}
                        } 
        	        	return result;
                    }
                }
                , {field: 'maintainFinishSolution', title: '维修完成措施与不良原因', width: 220, sort: false
                	, templet: function (obj) {
        	  	  		var result = '';
        	  	  		if(obj.maintainFinishSolution){
        	  	  			result += "措施："+obj.maintainFinishSolution + "。 ";
        	  	  		}
        	  	  		if(obj.maintainFinishBadReason){
        	  	  			result += "不良原因："+obj.maintainFinishBadReason + "。 ";
        	  	  		}
        	        	return result;
                    }
                }
                , {field: 'maintainOrgName', title: '出库维修公司', width: 120 , sort: false}
                , {field: 'orderCode', title: '出库维修包装流转单号', width: 190 , sort: false} 
                , {field: 'applyTimeAndName', title: '报修人与报修时间', width: 220, height: 315
                    , templet: function (obj) {
        	  	  		var result = '';
        		  		if(obj.maintainApplyRealName){
        		  			result += '<span class="tableCellPre">'+ obj.maintainApplyRealName +'</span>';
        		  		}
        		  		if(obj.maintainApplyTime){
        		  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.maintainApplyTime) +'</span>';
        		  		}
        	        	return result;
                    }
                }
                , {field: 'maintainCheckRealName', title: '维修鉴定人与鉴定时间', width: 220, height: 315
                    , templet: function (obj) {
        	  	  		var result = '';
        		  		if(obj.maintainCheckRealName){
        		  			result += '<span class="tableCellPre">'+ obj.maintainCheckRealName +'</span>';
        		  		}
        		  		if(obj.maintainCheckTime){
        		  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.maintainCheckTime) +'</span>';
        		  		}
        	        	return result;
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
        //列表数据重载
        var reloadTableData = function(currValue){
      	    layer.load(2); //加载中loading效果
	  	  	//菜单选中值
	  	  	var outMode = '';
		  	layui.$("input:checkbox[name='outModeCheckBox']").each(function () {
				var outModeCheckBox = this;
				if(outModeCheckBox.checked){
					outMode = '1';
				}  
	      	});
        	var reloadParam = {
                   where: {
                	   'epcId': $(".layui-search-form input[name='epcId']").val(),
                       'printCode': $(".layui-search-form input[name='printCode']").val(),
                       'maintainState': $(".layui-search-form select[name='maintainState']").val(),
                       'startDate': $(".layui-search-form").find("input[name='startDate']").val(),
                       'endDate': $(".layui-search-form").find("input[name='endDate']").val(),
                       'outMode': outMode
                   }
            };
          	if(currValue){
          		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
          	}
        	table.reload('layuiReloadId', reloadParam);
        }

        layui.$('.queryButton').on('click', function () {
        	reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
        });
      //重置按钮
        layui.$('.resetButton').on('click', function(){
        	  $("#containerMaintainPanel input[name='epcId']").val("");
        	  $("#containerMaintainPanel input[name='printCode']").val("");
        	  $("#containerMaintainPanel input[name='startDate']").val("");
        	  $("#containerMaintainPanel input[name='endDate']").val("");
        	  $("#containerMaintainPanel select[name='maintainState']").val("");
        	  layui.$("input:checkbox[name='outModeCheckBox']").each(function () { 
        		   this.checked = false;
		      	}) 
        	  form.render();
        });
		/*报修与报废申请*/
       layui.$('.addButton').on('click', function () {
                layerEditName = layer.open({
                    type: 1 //Page层类型
                    , area: ['400px', '300px']
                    , title: '器具报修'
                    , shade: 0.6 //遮罩透明度
                    , maxmin: true //允许全屏最小化
                    , anim: -1 //0-6的动画形式，-1不开启
                    , content: $("#applianceRepairPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                    , cancel: function () {
                        //alert("关闭啦");
                } 
                , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    form.render();
                }
                , btn: ['保存']
                , btn1: function (index) {
                    var param = {};
                    param['epcId'] = $(".applianceRepairPanelForm input[name='epcId']").val();
                    param['maintainApplyBadReason'] = $(".applianceRepairPanelForm textarea[name='maintainApplyBadReason']").val();
                    if (!param['epcId']) {
                        layer.msg("EPC编号必填");
                        return;
                    }
                    if (!param['maintainApplyBadReason']) {
                        layer.msg("不良原因必填");
                        return;
                    }
                    layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/maintain/addMaintain",
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
        });

        layui.$('.exportButton').on('click', function () {
            var epcId = $(".layui-search-form input[name='epcId']").val();
            var printCode = $(".layui-search-form input[name='printCode']").val();
            var maintainState = $(".layui-search-form select[name='maintainState']").val();
            var startDate = $(".layui-search-form").find("input[name='startDate']").val();
            var endDate = $(".layui-search-form").find("input[name='endDate']").val();
            var param = "?a=1";
            if (epcId){
                param+="&epcId="+epcId;
            }
            if (printCode){
                param+="&printCode="+printCode;
            }
            if (maintainState){
                param+="&maintainState="+maintainState;
            }
            if (startDate) {
                param += "&startDate=" + startDate;
            }
            if (endDate){
                param+="&endDate="+endDate;
            }
            window.open(basePath + '/maintain/expertToExcel'+param,'target','');

        });

        function maintainScrap(maintainId,version) {
            layerScrap = layer.open({
                type: 1 //Page层类型
                , area: ['500px', '400px']
                , title: '同意报废'
                , shade: 0.6 //遮罩透明度
                , maxmin: true //允许全屏最小化
                , anim: -1 //0-6的动画形式，-1不开启
                , content: $("#maintainScrapPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                , cancel: function () {
                    //alert("关闭啦");
                }
                , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    //初始化报废方式
                    layer.load(2); //加载中loading效果
                    $.ajax({
                        type: 'POST',
                        url: basePath + '/scrapWay/queryScrapWapList',
                        dataType: 'json',
                        success: function (data) {
                        	layer.closeAll('loading'); //关闭加载中loading效果
                            var states = data.list;
                            var option="";
                            for (var i=0;i<states.length;i++){
                                option += "<option value='" + states[i].scrapWayId + "'>" + states[i].scrapWayName+ "</option>";
                            }
                            layui.$(".maintainScrapPanelForm select[name='scrapWayName']").append(option);
                            layui.form.render();
                        },error: function(index, upload){
    		            	layer.closeAll('loading'); //关闭加载中loading效果
    		         	}
                    });
                }
                , btn: ['同意报废']
                , btn1: function (index) {
                    var scrapWayId = $(".maintainScrapPanelForm select[name='scrapWayName']").val();
                    var scrapWayName = $(".maintainScrapPanelForm select[name='scrapWayName'] option:selected").text();
                    var param = {};
                    if (!maintainId) {
                        layer.msg("维修器具Id为空");
                        return;
                    }
                    param['maintainId'] = maintainId;
                    if (!scrapWayId) {
                        layer.msg("报废方式必选");
                        return;
                    }
                    param['scrapWayId'] = scrapWayId;
                    param['scrapWayName'] = scrapWayName;
                    param['version'] = version;
                    layer.load(2); //加载中loading效果
    				$.ajax({
    					url: basePath + "/maintain/maintainScrap",
    					type: "POST",
    					dataType: "json",
    					data: param,
    					success: function (result, textStatus, jqXHR) {
    						layer.closeAll('loading'); //关闭加载中loading效果
    						layer.msg(result.msg);
    						if (result.status == 200) {
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

        //驳回维修
        function scrapWayReject(maintainId,version) {
            var param={};
            param['maintainId'] = maintainId;
            param['version'] = version;
            layer.load(2); //加载中loading效果
            $.ajax({
    			url: basePath + "/maintain/scrapWayReject",
    			type: "POST",
    			dataType: "json",
    			data: param,
    			success: function (result, textStatus, jqXHR) {
    				layer.closeAll('loading'); //关闭加载中loading效果
    				layer.msg(result.msg);
    				if (result.status == 200) {
    					layer.closeAll('page');   //成功后，关闭所有弹出框
    					reloadTableData(); //重新加载table数据
    				}
    			},error: function(index, upload){
                	layer.closeAll('loading'); //关闭加载中loading效果
             	}
    		});
        }
        //监听工具条
        /* table.on('tool(myLayFilter)', function(obj){
            var data = obj.data;
            if(obj.event === 'maintainFinshEvent'){
            	maintainFinsh(data.maintainId,data.version); //维修完成
            }else if(obj.event === 'maintainScrapEvent'){
            	maintainScrap(data.maintainId,data.version); //同意报废
            }else if(obj.event === 'scrapWayRejectEvent'){
            	scrapWayReject(data.maintainId,data.version); //报废驳回
            }
        }); */
        
        table.on('tool(myLayFilter)', function(obj){
            var data = obj.data;
            if(obj.event === 'maintainAppraisal'){
            	maintainAppraisal(data); //维修鉴定
            }else if(obj.event === 'maintainFinish'){maintainAppraisal
            	maintainFinish(data); //维修完成
            }
        });
        /* ================================维修鉴定========================= */
        var maintainAppraisal = function(data){
            var layerEditName = layer.open({
                type: 1 //Page层类型
                ,area: ['450px', '500px']
                ,title: '维修鉴定'
                ,shade: 0.6 //遮罩透明度
                ,maxmin: true //允许全屏最小化
                ,anim: -1 //0-6的动画形式，-1不开启
                ,content: $("#maintainAppraisalIdPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                ,cancel: function(){
                    //alert("关闭啦");
                }
                ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                	//初始化epcId的值
                	$(".maintainAppraisalIdPanelForm input[name='epcId']").val(data.epcId);
                	//初始化维修级别
                    $.ajax({
                        type: 'POST',
                        url: basePath + '/mainTainLevel/queryMaintainLevelList',
                        dataType: 'json',
                        success: function (data) {  
                        	 var states = data.list;
                        	 for (var i=0;i<states.length;i++){
                              	var option = '<input type="radio" name="maintainLevel" value="'+states[i].maintainLevel+'" title="'+states[i].maintainLevel+' （'+states[i].maintainLevelName+'）"> <br/>';
                              	$(".appendMaintainLevelLists").append(option + '<br/>');
                            }
                            form.render();
                        }
                    });
				 }
                ,btn: ['保存']
                ,btn1: function(index){
                	var param = {};
                	param["epcId"] = data.epcId;
                	param["maintainId"] = data.maintainId;
                	$(".maintainAppraisalIdPanelForm input:radio[name='maintainLevel']").each(function () { 
                		if(this.checked){
                    		param['maintainLevel'] = this.value; //获取单选框的值
                		}
           	        })
                     if (!param['maintainLevel']) {
                         layer.msg("维修级别必选");
                         return;
                     }
                     layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/maintain/maintainAppraisal",
                        type:"POST",
                        dataType: "json",
                        data: param,
                        success: function(result, textStatus, jqXHR){
                            layer.msg(result.msg);
                            if(result.status == 200){
                                layer.closeAll('page');   //成功后，关闭所有弹出框
                                reloadTableData(); //重新加载table数据
                            }
                            layer.closeAll('loading'); //关闭加载中loading效果
					  	  },error: function(index, upload){
				            	layer.closeAll('loading'); //关闭加载中loading效果
				          }
                    });
                }
            });
        }
        /* ================================维修完成========================= */
                var maintainFinish = function(data){
                    var layerEditName = layer.open({
                        type: 1 //Page层类型
                        ,area: ['450px', '450px']
                        ,title: '维修完成'
                        ,shade: 0.6 //遮罩透明度
                        ,maxmin: true //允许全屏最小化
                        ,anim: -1 //0-6的动画形式，-1不开启
                        ,content: $("#mainFinishIdPanel").html()
                        ,cancel: function(){
                            //alert("关闭啦");
                        }
                        ,success: function (layero, index) {
                        	form.render();
                        	$(".mainFinishIdPanelForm input[name='epcId']").val(data.epcId);
        				 }
                        ,btn: ['保存']
                        ,btn1: function(index){
                        	var param = {};
                        	param['maintainId'] = data.maintainId;
                            param['version'] = data.version;
                            param["maintainFinishSolution"] = $(".mainFinishIdPanelForm textarea[name='maintainFinishSolution']").val();
                            param["maintainFinishBadReason"] = $(".mainFinishIdPanelForm textarea[name='maintainFinishBadReason']").val();
                             layer.load(2); //加载中loading效果
                            $.ajax({
                                url: basePath + "/maintain/maintainFinish",
                                type:"POST",
                                dataType: "json",
                                data: param,
                                success: function(result, textStatus, jqXHR){
                                    layer.msg(result.msg);
                                    if(result.status == 200){
                                        layer.closeAll('page');   //成功后，关闭所有弹出框
                                        reloadTableData(); //重新加载table数据
                                    }
                                    layer.closeAll('loading'); //关闭加载中loading效果
        					  	  },error: function(index, upload){
        				            	layer.closeAll('loading'); //关闭加载中loading效果
        				      }
                        });
                   }
               });
          }
    });
</script>
</html>