<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>器具列表</title>
</head>
<body>

	<%------------------------------------------------
                    中间内容开始
------------------------------------------------%>
    <div class="layui-form layui-search-form">
		<div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">EPC编号</label>
                <div class="layui-input-inline">
                    <input type="text" name="epcId" lay-verify="required" autocomplete="on" class="layui-input">
                </div>
				<label class="layui-form-label">隶属仓库</label>
				<div class="layui-input-inline">
					<select name="belongOrgId" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
				<label class="layui-form-label">流转仓库</label>
				<div class="layui-input-inline">
					<select name="lastOrgId" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
            </div>
            <div class="layui-inline">
                <label class="layui-form-label">器具代码</label>
                <div class="layui-input-inline">
                    <select name="containerCode" lay-filter="containerCodeFilter" lay-search>
                    <option value="">请选择</option>
                    </select>
                </div>
                <label class="layui-form-label">器具类型</label>
                <div class="layui-input-inline">
                    <select name="containerTypeName" lay-search>
                        <option value="">请选择</option>
                    </select>
                </div>

				<div class="layui-inline">
      				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
      				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
      				<cmc:button buttonId="1"> <!-- 用户有该按钮权限才会显示以下代码 -->
                    	<button class="layui-btn layui-btn-sm addButton" data-type="">新增</button>
                    </cmc:button>
                    <cmc:button buttonId="2"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button class="layui-btn layui-btn-sm" onclick="downloadTemplate('/container/excelDownload')">导入模版下载</button>
					</cmc:button>
                    <cmc:button buttonId="3"> <!-- 用户有该按钮权限才会显示以下代码 -->
                    	<button type="button" class="layui-btn layui-btn-sm" id="uploadExcel">
                    	<i class="layui-icon"></i>批量导入
                    	</button>
                    </cmc:button>
                    <cmc:button buttonId="88"> <!-- 用户有该按钮权限才会显示以下代码 -->
                    <button type="button" class="layui-btn layui-btn-sm exportButton" id="exportButton">导出当前查询结果</button>
                    </cmc:button>
            </div>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

		<script type="text/html" id="handleBar">
			<cmc:button buttonId="5">
            	<a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
			</cmc:button>
			<cmc:button buttonId="6">
            	<a class="layui-btn layui-btn-xs" lay-event="outModel">过时</a>
			</cmc:button>
        </script>
	</div>

<%------------------------------------------------
                    中间内容结束
------------------------------------------------%>
	</div>
	</div>
	</div>
</body>
</html>
<!-- 器具新增弹出框 -->
<script type="text/html" id="addContainerPanel">
    <div class="layui-form layerForm addContainerPanelForm myLabelWidth125" action="">
        <div class="layui-form-item">
			<div class="layui-inline">
            	<label class="layui-form-label">EPC编号<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
               		<input type="text" name="epcId" placeholder="此项必填" autocomplete="off" class="layui-input">
            	</div>
            	<label class="layui-form-label">EPC类型<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
                	<input type="text" name="epcType" lay-verify="required" placeholder="此项必填" autocomplete="off" class="layui-input">
            	</div>
			</div>
        </div>
        <div class="layui-form-item">
        </div>
        <div  class="layui-form-item">
            <label class="layui-form-label">器具代码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <select name="containerCode" lay-filter="addContainerPanelForm-containerCodeFilter" lay-search>
                    <option value="">此项必选</option>
                </select>
            </div>
            <label class="layui-form-label">印刷编号</label>
            <div class="layui-input-inline">
                <input type="text" name="printCode"  autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">器具类型<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="containerTypeName" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled>
            </div>
            <label class="layui-form-label">器具名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="containerName" id="containerName" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">是否托盘</label>
            <div class="layui-input-inline">
					<input type="radio" name="isTray" value="0" title="不是"  class="layui-disabled" disabled>
    			    <input type="radio" name="isTray" value="1" title="是"  class="layui-disabled" disabled>
            </div>
            <label class="layui-form-label">材质</label>
            <div class="layui-input-inline">
                <input type="text" name="containerTexture" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">是否单独成托<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
					<input type="radio" name="isAloneGroup" value="0" title="不是">
    			    <input type="radio" name="isAloneGroup" value="1" title="是">
            </div>
            <label class="layui-form-label">尺寸</label>
            <div class="layui-input-inline">
                <input type="text" name="containerSpecification" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled >
            </div>
        </div>
    </div>
</script>

<!-- 器具修改弹出框 -->
<script type="text/html" id="editContainerPanel">
    <div class="layui-form layerForm editContainerPanelForm myLabelWidth125" action="">
        <div class="layui-form-item">
			<div class="layui-inline">
            	<label class="layui-form-label">EPC编号<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
               		<input type="text" name="epcId" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled"  disabled="disabled">
            	</div>
            	<label class="layui-form-label">EPC类型<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
                	<input type="text" name="epcType" lay-verify="required" placeholder="此项必填" autocomplete="off" class="layui-input">
            	</div>
			</div>
        </div>
        <div class="layui-form-item">
        </div>
        <div  class="layui-form-item">
            <label class="layui-form-label">器具代码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <select name="containerCode" lay-filter="editContainerPanelForm-containerCodeFilter" lay-search>
                    <option value="">此项必选</option>
                </select>
            </div>
            <label class="layui-form-label">印刷编号</label>
            <div class="layui-input-inline">
                <input type="text" name="printCode"  autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">器具类型<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="containerTypeName" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled>
            </div>
            <label class="layui-form-label">器具名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="containerName" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">是否托盘</label>
            <div class="layui-input-inline">
					<input type="radio" name="isTray" value="0" title="不是"  class="layui-disabled" disabled>
    			    <input type="radio" name="isTray" value="1" title="是"  class="layui-disabled" disabled>
            </div>
            <label class="layui-form-label">材质</label>
            <div class="layui-input-inline">
                <input type="text" name="containerTexture" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">是否单独成托<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
					<input type="radio" name="isAloneGroup" value="0" title="不是">
    			    <input type="radio" name="isAloneGroup" value="1" title="是" >
            </div>
            <label class="layui-form-label">尺寸</label>
            <div class="layui-input-inline">
                <input type="text" name="containerSpecification" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled >
            </div>
        </div>
        <div>
            <label class="layui-form-label"></label>
            <div class="layui-input-inline" >
                <img id="containerPictrue" src='' style="margin-right: 15px;"/>
                <button type="button" class="layui-btn containerPic"><i class="layui-icon"></i>更新图片</button>
            </div>
        </div>
    </div>
</script>
<!-- 器具过时弹出框 -->
<script type="text/html" id="containerOutModePanel">
    <div class="layui-form containerOutModePanelForm layerForm myLabelWidth115" action="">
        <div class="layui-form-item">
			<div class="layui-inline">
            	<label class="layui-form-label">EPC编号<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
               		<input type="text" name="epcId" class="layui-input layui-disabled"  disabled="disabled">
            	</div>
			</div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">合同号<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="contractNumber" lay-verify="required" autocomplete="on" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">领用单号<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="receiveNumber" lay-verify="required" autocomplete="on" class="layui-input">
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

    //初始化隶属公司下拉选择框
	$.ajax({
	  	  url: basePath + "/org/filialeSystemOrgList",
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {},
	  	  success: function(result, textStatus, jqXHR){
	  		    var options = "<option value=\"\">直接选择或搜索选择</option>";
	  		    var list = result.list;
	  		  	orgList = list; //赋值给全局变量
		  		$.each( list, function(i, obj){
		  		  	options += "<option value=\""+obj.orgId+"\">"+obj.orgName+"</option>";
		  		});
		  		$("select[name='belongOrgId']").html(options);
		  		$("select[name='lastOrgId']").html(options);
		  		form.render();
	  	  }
	});

    //初始化器具代码select框
    var containerCodeList = [];
    $.ajax({
        type: 'POST',
        url: basePath + '/containerCode/listAllContainerCode',
        dataType: 'json',
        success: function (data) {
            containerCodeList = data.list;
            var containerCoderOption = "<option value=''>直接选择或搜索选择</option>";
            for (var i=0;i<containerCodeList.length;i++){
                containerCoderOption += "<option value='" + containerCodeList[i].containerCode + "'>" + containerCodeList[i].containerCode+ "</option>";
            }
            $(".layui-search-form select[name='containerCode']").html(containerCoderOption);
            form.render();
        }
    });

    //初始化器具代码select框，此器具代码列表排除了已被禁用的器具代码
    var activeContainerCodeList = [];
    $.ajax({
        type: 'POST',
        url: basePath + '/containerCode/listActiveContainerCode',
        dataType: 'json',
        success: function (data) {
        	activeContainerCodeList = data.list;
            var containerCoderOption = "<option value=''>直接选择或搜索选择</option>";
            for (var i=0;i<activeContainerCodeList.length;i++){
                containerCoderOption += "<option value='" + activeContainerCodeList[i].containerCode + "'>" + activeContainerCodeList[i].containerCode+ "</option>";
            }
            $(".layui-search-form select[name='containerCode']").html(containerCoderOption);
            form.render();
        }
    });

    //加载所有器具类型
    var containerTypeList = [];
    $.ajax({
        type: 'POST',
        url: basePath + '/containerType/listAllContainerType',
        dataType: 'json',
        success: function (data) {
        	containerTypeList = data;
            var options = "<option value=''>直接选择或搜索选择</option>";
            for (var i=0;i<data.length;i++){
                options += "<option value='" + data[i].containerTypeId + "'>" + data[i].containerTypeName+ "</option>";
            }
            $(".layui-search-form select[name='containerTypeName']").html(options);
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
    	}
        , url: basePath + '/container/pagingContainer' //数据接口
        , cols: [[
             {field: 'epcId', title: 'EPC编号', width: 170, sort: false}
            , {field: 'containerCode', title: '器具代码', width: 120, sort: false}
            , {field: 'containerTypeName', title: '器具类型', width: 120 , sort: false}
            , {field: 'containerName', title: '器具名称', width: 160 , sort: false}
            , {title: '操作', width: 120, sort: false, align: 'center', toolbar: '#handleBar'}
            ,{field: 'isTray', width: 120, title: '是否是托盘', sort: false,templet: function(obj){
		  		//obj.isTray 是否是托盘，0不是，1是
	  			var result = '<input type="checkbox" value='+obj.isTray+' name="isTray" lay-skin="switch" lay-filter="isTraySwitch" lay-text="是|否" class="layui-disabled" disabled '
			         +((obj.isTray && obj.isTray == 1) ? 'checked' : '')+'  '+'  >';
		  		return result;
		  }}
            , {field: 'containerSpecification', title: '尺寸', width: 120 , sort: false}
            , {field: 'belongOrgName', title: '隶属仓库', width: 190, sort: false}
            , {field: 'lastOrgName', title: '流转仓库', width: 190, sort: false}
            , {field: 'epcType', title: 'EPC类型', width: 140, sort: false}
            , {field: 'containerTexture', title: '材质', width: 90 , sort: false}
            , {field: 'picUrl', title: '图片', width: 90 , sort: false,
                templet: function (d) { //+data.epcId+"&d="+DateUtil.todayMillisecond()
                        return "<img src='"+basePath+"/container/showContainerPictrue?epcId="+d.epcId+"&d="+DateUtil.todayMillisecond()+"'/>";
                }
            }
            , {field: 'printCode', title: '印刷编号', width: 140, sort: false}
            , {field: 'isAloneGroup', title: '是否单独成托', width: 120 , sort: false,
                templet: function (d) {
                    if(d.isAloneGroup == 1){ //是否单独成托，0不是，1是
                        return "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#xe618;</i>";
                    } else if(d.isAloneGroup == 0){
                        return "非单独成托";
                    } else {
                        return "";
                    }
                }
            }
            , {field: 'creatTimeAndName', title: '创建人与创建时间', width: 250
                , templet: function (obj) {
    	  	  		var result = '';
    		  		if(obj.createRealName){
    		  			result += '<span class="tableCellPre">'+ obj.createRealName +'</span>';
    		  		}
    		  		if(obj.createTime){
    		  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
    		  		}
    	        	return result;
                }
            }
            , {
                field: 'updateTimeAndName', title: '修改人与修改时间', width: 250
                , templet: function (d) {
                    if (d.modifyTime == null && d.modifyRealName == null) {
                        return "";
                    } else {
                        return '<span>' + d.modifyRealName + '</span>&nbsp;<span>' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.modifyTime);
                    }
                }
            }
            , {field: 'createOrgName', title: '创建仓库', width: 190, sort: false}
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
    	var reloadParam = {
               where: {
            	   'epcId': $(".layui-search-form input[name='epcId']").val(),
                   'printCode': $(".layui-search-form input[name='printCode']").val(),
                   'containerTypeId': $(".layui-search-form select[name='containerTypeName']").val(),
                   'containerCode': $(".layui-search-form select[name='containerCode']").val(),
                   'belongOrgId': $(".layui-search-form select[name='belongOrgId']").val(),
                   'lastOrgId': $(".layui-search-form select[name='lastOrgId']").val()
               }
        };
      	if(currValue){
      		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
      	}
    	table.reload('layuiReloadId', reloadParam);
    }

    //"查询"按钮点击
    layui.$('.queryButton').on('click', function () {
    	reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
    });
 	 //重置按钮
    $('.resetButton').on('click', function(){
    	  $(".layui-search-form input[name='epcId']").val("");
    	  $(".layui-search-form input[name='printCode']").val("");
    	  $(".layui-search-form select[name='containerTypeName']").val("");
    	  $(".layui-search-form select[name='containerCode']").val("");
    	  $(".layui-search-form select[name='lastOrgId']").val("");
    	  $(".layui-search-form select[name='belongOrgId']").val("");
    	  form.render();
    });

    //监听工具条
    table.on('tool(myLayFilter)', function (obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
            editJobNameFunction(data);
        } else if (obj.event === 'outModel') {
            outModel(data)
        }
    });

    //table表格列里的“过时”按钮点击
    var outModel = function (data) {
        layerEditName = layer.open({
            type: 1 //Page层类型
            , area: ['400px', '300px']
            , title: '过时器具'
            , shade: 0.6 //遮罩透明度
            , maxmin: true //允许全屏最小化
            , anim: -1 //0-6的动画形式，-1不开启
            , content: $("#containerOutModePanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            , cancel: function () {
                //alert("关闭啦");
            }
            , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
            	$(".containerOutModePanelForm input[name='epcId']").val(data.epcId);
                form.render();
            }
            , btn: ['保存']
            , btn1: function (index) {
                var param = {};
                param['epcId'] = data.epcId;
                param['version'] = data.version;
                param['contractNumber'] = $(".containerOutModePanelForm input[name='contractNumber']").val();
                param['receiveNumber'] = $(".containerOutModePanelForm input[name='receiveNumber']").val();
                if (!param['contractNumber']) {
                    layer.msg("合同号必填");
                    return;
                }
                if (!param['receiveNumber']) {
                    layer.msg("领用单号必填");
                    return;
                }
          	    layer.load(2); //加载中loading效果
                $.ajax({
                    url: basePath + "/containerOutMode/addOutmode",
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
    //新增按钮点击
    layui.$('.addButton').on('click', function () {
        layerEditName = layer.open({
            type: 1 //Page层类型
            , area: ['800px', '450px']
            , title: '新增器具'
            , shade: 0.6 //遮罩透明度
            , maxmin: true //允许全屏最小化
            , anim: -1 //0-6的动画形式，-1不开启
            , content: $("#addContainerPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            , cancel: function () {
                //alert("关闭啦");
            }
            , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
            	var containerCoderOption = "<option value=''>直接选择或搜索选择</option>";
                for (var i=0;i<activeContainerCodeList.length;i++){
                    containerCoderOption += "<option value='" + activeContainerCodeList[i].containerCode + "'>" + activeContainerCodeList[i].containerCode+ "</option>";
                }
                $(".addContainerPanelForm select[name='containerCode']").html(containerCoderOption);
                form.render();
            }
            , btn: ['保存']
            , btn1: function (index) {
                var param = {};
                param['epcId'] = $(".addContainerPanelForm input[name='epcId']").val();
                param['epcType'] = $(".addContainerPanelForm input[name='epcType']").val();
                param['containerName'] = $(".addContainerPanelForm input[name='containerName']").val();
                param['printCode'] = $(".addContainerPanelForm input[name='printCode']").val();
                param['containerTypeName'] = $(".addContainerPanelForm input[name='containerTypeName']").val();
                param['containerCode'] = $(".addContainerPanelForm select[name='containerCode']").val();
                param['containerSpecification'] = $(".addContainerPanelForm input[name='containerSpecification']").val();
                param['containerTexture'] = $(".addContainerPanelForm input[name='containerTexture']").val();
                $(".addContainerPanelForm input:radio[name='isAloneGroup']").each(function () {
           		 	if(this.checked){
               			param['isAloneGroup'] = this.value; //获取单选框的值：是否单独成托
           		 	}
      	        });
                $(".addContainerPanelForm input:radio[name='isTray']").each(function () {
           		 	if(this.checked){
               			param['isTray'] = this.value; //获取单选框的值：是否是托盘
           		 	}
      	        });
                if (!param['epcId']) {
                    layer.msg("EPC编号不能为空");
                    return;
                }
                if (!param['epcType']) {
                    layer.msg("EPC类型必填");
                    return;
                }
                if (!param['containerName']) {
                    layer.msg("器具名称必填");
                    return;
                }
                if (!param['containerTypeName']) {
                    layer.msg("器具类型必填");
                    return;
                }
                if (!param['containerCode']) {
                    layer.msg("器具代码必填");
                    return;
                }
                if (!param['isAloneGroup']) {
                    layer.msg("是否单独承托必选");
                    return;
                }
          	    layer.load(2); //加载中loading效果
                $.ajax({
                    url: basePath + "/container/addContainer",
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

    //编辑器具
    var editJobNameFunction = function (data) {
        var layerEditName = layer.open({
            type: 1 //Page层类型
            , area: ['800px', '500px']
            , title: '编辑器具'
            , shade: 0.6 //遮罩透明度
            , maxmin: true //允许全屏最小化
            , anim: -1 //0-6的动画形式，-1不开启
            , content: $("#editContainerPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            , cancel: function () {
                //alert("关闭啦");
            }
            , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                $(".editContainerPanelForm input[name='epcId']").val(data.epcId);
                $(".editContainerPanelForm input[name='epcType']").val(data.epcType);
                $(".editContainerPanelForm input[name='containerName']").val(data.containerName);
                $(".editContainerPanelForm input[name='printCode']").val(data.printCode);
                $(".editContainerPanelForm input[name='containerTypeName']").val(data.containerTypeName);
                $(".editContainerPanelForm input[name='containerSpecification']").val(data.containerSpecification);
                $(".editContainerPanelForm input[name='containerTexture']").val(data.containerTexture);
                $(".editContainerPanelForm input[name='isTray2']").val(data.isTray);
                $(".editContainerPanelForm input[name='isTrayName2']").val(data.isTray==1?'是':'否');
				//初始化器具代码下拉框
            	var containerCoderOption = "<option value=''>直接选择或搜索选择</option>";
                for (var i=0;i<containerCodeList.length;i++){
                	var selected = "";
                	if(data.containerCode == containerCodeList[i].containerCode){
                		selected = " selected ";
                	}
                    containerCoderOption += "<option value='" + containerCodeList[i].containerCode + "' "+selected+">" + containerCodeList[i].containerCode+ "</option>";
                }
                $(".editContainerPanelForm select[name='containerCode']").html(containerCoderOption);
                $(".editContainerPanelForm select[name='containerCode']").val(data.containerCode);
                //$(".editContainerPanelForm select[name='containerSpecification']").val(data.containerSpecification);
                //是否单独成托，设置值
               $(".editContainerPanelForm input:radio[name='isAloneGroup']").each(function () {
           		 	if(this.value == data.isAloneGroup){
           		 		this.checked = true; //设置选中值
           		 	}else{
           		 		this.checked = false;
           		 	}
      	        })
               //是否托盘，设置值
               $(".editContainerPanelForm input:radio[name='isTray']").each(function () {
           		 	if(this.value == data.isTray){
           		 		this.checked = true; //设置选中值
           		 	}else{
           		 		this.checked = false;
           		 	}
      	        })
               form.render();
                $("#containerPictrue").attr("src",basePath+"/container/showContainerPictrue?epcId="+data.epcId+"&d="+DateUtil.todayMillisecond());
                form.render();

                //图片上传
                var picUpload = upload.render({
                    elem: '.editContainerPanelForm .containerPic',
                    data:{'epcId':data.epcId}
                    , url: basePath + '/container/uploadImage'
                    , accept: 'images' //指定允许上传的文件类型，可选值有：images（图片）、file（所有文件）、video（视频）、audio（音频）
                    , size:5000 //设置文件最大可允许上传的大小，单位 KB。不支持ie8/9
                   	,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                       	layer.load(2); //加载中loading效果
                    }
                    ,done: function(result, upload){
                    	layer.closeAll('loading'); //关闭加载中loading效果
                    	layer.msg(result.msg);
                    	if(result.status == 200){ //如果更新器具图片成功
                    		//刷新器具图片
                            $("#containerPictrue").attr("src",basePath+"/container/showContainerPictrue?epcId="+data.epcId+"&d="+DateUtil.todayMillisecond());
                            form.render();
                        	reloadTableData(); //重新加载table数据
                    	}
                    }
                    ,error: function(index, upload){
                    	layer.closeAll('loading'); //关闭加载中loading效果
                    }
                });
                form.render();
            }
            , btn: ['保存']
            , btn1: function (index) {
                var param = {};
                param['version'] = data.version;
                param['epcId'] = $(".editContainerPanelForm input[name='epcId']").val();
                param['epcType'] = $(".editContainerPanelForm input[name='epcType']").val();
                param['containerName'] = $(".editContainerPanelForm input[name='containerName']").val();
                param['printCode'] = $(".editContainerPanelForm input[name='printCode']").val();
                param['containerTypeName'] = $(".editContainerPanelForm input[name='containerTypeName']").val();
                param['containerCode'] = $(".editContainerPanelForm select[name='containerCode']").val();
                param['containerSpecification'] = $(".editContainerPanelForm input[name='containerSpecification']").val();
                param['containerTexture'] = $(".editContainerPanelForm input[name='containerTexture']").val();
                param['isTray'] = $(".editContainerPanelForm input[name='isTray2']").val();
                $(".editContainerPanelForm input:radio[name='isAloneGroup']").each(function () {
           		 	if(this.checked){
               			param['isAloneGroup'] = this.value; //获取单选框的值：是否单独成托
           		 	}
      	        });
                $(".editContainerPanelForm input:radio[name='isTray']").each(function () {
           		 	if(this.checked){
               			param['isTray'] = this.value; //获取单选框的值：是否是托盘
           		 	}
      	        });
                if (!param['epcId']) {
                    layer.msg("EPC编号必填");
                    return;
                }
                if (!param['epcType']) {
                    layer.msg("EPC类型必填");
                    return;
                }
                if (!param['containerName']) {
                    layer.msg("器具名称必填");
                    return;
                }
                if (!param['containerTypeName']) {
                    layer.msg("器具类型必填");
                    return;
                }
                if (!param['containerCode']) {
                    layer.msg("器具代码必填");
                    return;
                }
          	    layer.load(2); //加载中loading效果
                $.ajax({
                    url: basePath + "/container/updateContainer",
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

    //"批量上传"按钮点击
    upload.render({
        elem: '#uploadExcel'
        , url: basePath + '/container/batchUploadContainer'
        , accept: 'file' //普通文件
       	,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
           	layer.load(2); //加载中loading效果
        }
        ,done: function (result) {
        	layer.closeAll('loading'); //关闭加载中loading效果
            if (result.status == 200) {
                layer.msg(result.msg);
                reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
            }else{
            	  //如果有错误列表，则显示错误列表，否则显示错误提示信息
            	  if(result.list && result.list.length > 0){ 
		              	var errMsg = "";
		              	$.each( result.list, function(i, value){
		              		errMsg += value +"<br/>";
		             		});
		              	layer.alert(errMsg); 
            	  } else{ 
		              	layer.alert(result.msg); 
            	  }
          }
        }
        ,error: function(index, upload){
        	layer.closeAll('loading'); //关闭加载中loading效果
        }
    });

    //导出
    layui.$('.exportButton').on('click', function () {
        var param ="?a=1";
        var epcId = $(".layui-search-form input[name='epcId']").val();
        var printCode = $(".layui-search-form input[name='printCode']").val();
        var containerTypeName = $(".layui-search-form select[name='containerTypeName']").val();
        var containerCode = $(".layui-search-form select[name='containerCode']").val();
        if (epcId){
            param+="&epcId="+epcId;
        }
        if (printCode) {
            param += "&printCode=" + printCode;
        }
        if (containerTypeName){
            param+="&containerTypeName="+containerTypeName;
        }
        if (containerCode){
            param+="&containerCode="+containerCode;
        }
        window.open(basePath + '/container/expertToExcelContainer'+param,'targer','');

    });
    
    form.on('select(addContainerPanelForm-containerCodeFilter)', function(data){
    	  var containerCode = data.value;
    	  $.ajax({
              type:'POST',
              dataType:'json',
              url: basePath + '/containerCode/queryByContainerCode',
              data:{containerCode:containerCode},
              success: function (data) {
                  console.log(data.bean);
                  $(".addContainerPanelForm input[name='containerName']").val(data.bean.containerName);
                  $(".addContainerPanelForm input[name='containerTypeName']").val(data.bean.containerTypeName);
                  $(".addContainerPanelForm input[name='containerSpecification']").val(data.bean.containerSpecification);
                  //是否托盘，设置值
                 $(".addContainerPanelForm input:radio[name='isTray']").each(function () { 
             		 	if(this.value == data.bean.isTray){
             		 		this.checked = true; //设置选中值
             		 	}else{
             		 		this.checked = false; 
             		 	}
        	      })
                  form.render();
              }
          })
    });

    form.on('select(editContainerPanelForm-containerCodeFilter)', function(data){
        var containerCode = data.value;
        $.ajax({
            type:'POST',
            dataType:'json',
            url: basePath + '/containerCode/queryByContainerCode',
            data:{containerCode:containerCode},
            success: function (data) {
                console.log(data.bean);
                $(".editContainerPanelForm input[name='containerName']").val(data.bean.containerName);
                $(".editContainerPanelForm input[name='containerTypeName']").val(data.bean.containerTypeName);
                $(".editContainerPanelForm input[name='containerSpecification']").val(data.bean.containerSpecification);
                //是否托盘，设置值
                $(".editContainerPanelForm input:radio[name='isTray']").each(function () { 
            		 	if(this.value == data.bean.isTray){
            		 		this.checked = true; //设置选中值
            		 	}else{
            		 		this.checked = false; 
            		 	}
       	        })
                form.render();
            }
        })
    });
});
</script>