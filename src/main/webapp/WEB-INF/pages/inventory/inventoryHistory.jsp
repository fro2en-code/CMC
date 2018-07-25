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
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<cmc:button buttonId="81"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm exportButton" data-type="reload">导出当前查询结果</button>
				</cmc:button>
				<cmc:button buttonId="90"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm adjustButton">库存调整</button>
				</cmc:button>
				<cmc:button buttonId="91"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm initInOrgNumberButton">excel批量初始化库存数量</button>
				</cmc:button>
				<cmc:button buttonId="92"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm" onclick="downloadTemplate('/inventory/excelDownload')">批量初始化库存-模板下载</button>
				</cmc:button>
			</div>
		</div>
	</div>
			<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
			<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
		</div>
</body>

<!-- 库存调整弹出框 -->
<script type="text/html" id="adjustPanel">
    <div class="layui-form adjustPanelForm layerForm myLabelWidth115" action="">
		<div class="layui-form-item">
            <label class="layui-form-label">仓库<span class="myRedColor">*</span></label>
            <div class="layui-input-inline" style="width:500px;">
					<select name="orgSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
            </div>
        </div>
        <div  class="layui-form-item">
            <label class="layui-form-label">器具代码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline" style="width:500px;">
                <select name="containerCode" lay-search>
                    <option value="">此项必选</option>
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">加减库存<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="adjustNumber" autocomplete="on" class="layui-input"  style="width:150px;">
            </div>
            <div class="layui-input-inline">
				<input type="radio" name="adjustRadio" value="1" title="加数量" checked>
      			<input type="radio" name="adjustRadio" value="0" title="减数量">
            </div>
        </div>
 		<div class="layui-form-item layui-form-text">
    		<label class="layui-form-label">备注<span class="myRedColor">*</span></label>
    		<div class="layui-input-inline" style="width:500px;">
      			<textarea name="remark" placeholder="请输入库存调整备注" class="layui-textarea"></textarea>
    		</div>
  		</div>
    </div>
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
        
        //"excel批量初始化库存数量"按钮点击
        upload.render({
            elem: '.initInOrgNumberButton'
            , url: basePath + '/inventory/batchInitInOrgNumber'
            , accept: 'file' //普通文件
           	,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
               	layer.load(2); //加载中loading效果
            }
            ,done: function (result) {
            	layer.closeAll('loading'); //关闭加载中loading效果
                if (result.status == 200) {
                    layer.msg(result.msg);
                    layui.$('.layui-search-form .queryButton').click();
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
        

        //初始化器具代码select框
        var containerCodeList = [];
        $.ajax({
            type: 'POST',
            url: basePath + '/containerCode/listAllContainerCode',
            dataType: 'json',
            success: function (data) {
                containerCodeList = data.list;
            }
        });
        
          //初始化机构选择数据
          var orgList = []; 
	   	  $.ajax({
	   	  	  url: basePath + "/org/filialeSystemOrgList",
	   	  	  type:"POST",
	   	  	  dataType: "json",
	   	  	  data: {},
	   	  	  success: function(result, textStatus, jqXHR){
	   	  		    var options = "<option value=\"\">直接选择或搜索选择</option>";
	   	  		    var list = result.list;
	   	  		  	orgList = list; //赋值给全局变量
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
            ,url:  basePath + '/inventory/pagingInventoryHistory'
            ,cols: [[
	               		{type:'numbers',title: '序号',width:60, sort: false}
		              ,{field: 'orgName', title: '仓库名称', width:250, sort: false}
		              ,{field: 'containerCode', title: '器具代码', width:100, sort: false}
		              ,{field: 'containerTypeName', title: '器具类型', width:130, sort: false}
		              ,{field: 'receiveNumber', title: '收货数量', width:90, sort: false}
		              ,{field: 'sendNumber', title: '发货数量', width:90, sort: false}
		              ,{field: 'inOrgNumber', title: '在库数量', width:90, sort: false}
		              , {field: 'createTime', title: '创建时间', width: 180
	                  , templet: function (obj) {
	      	  	  		var result = '';
	      		  		if(obj.createTime){
	      		  			result += '<span class="tableCellPre">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
	      		  		}
	      	        	return result;
	                  }
	              }
	              ,{field: 'orderCode', title: '流转单号', width:200, sort: false}
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
        //导出当前查询结果
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
	        window.open(basePath + '/inventory/expertExcelForInventoryHistory' +param,'target','');
	    });
        //库存调整
	    layui.$('.adjustButton').on('click', function () {
	    	layerEditName = layer.open({
	            type: 1 //Page层类型
	            , area: ['800px', '500px']
	            , title: '库存调整'
	            , shade: 0.6 //遮罩透明度
	            , maxmin: true //允许全屏最小化
	            , anim: -1 //0-6的动画形式，-1不开启
	            , content: $("#adjustPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
	            , cancel: function () {
	                //alert("关闭啦");
	            }
	            , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
					//初始化器具代码
	                var containerCoderOption = "<option value=''>直接选择或搜索选择</option>";
	                for (var i=0;i<containerCodeList.length;i++){
	                    containerCoderOption += "<option value='" + containerCodeList[i].containerCode + "'>" + containerCodeList[i].containerCode+ "</option>";
	                }
	                $(".adjustPanelForm select[name='containerCode']").html(containerCoderOption);
	                
	                //初始化仓库
					var defaultOrg = orgList[0].orgId
	            	var options = "<option value=''>直接选择或搜索仓库</option>";
	          		$.each( orgList, function(i, obj){
	          			options += "<option value='"+obj.orgId+"'>"+obj.orgName+"</option>";
	          		});
	          		$(".adjustPanelForm select[name='orgSelect']").html(options);
                    $(".adjustPanelForm select[name='orgSelect']").val(defaultOrg);
	          		form.render();
	            }
	            , btn: ['保存']
	            , btn1: function (index) {
	                var param = {};
	                param['orgId'] = $(".adjustPanelForm select[name='orgSelect']").val();
	                param['containerCode'] = $(".adjustPanelForm select[name='containerCode']").val();
	                param['adjustNumber'] = $(".adjustPanelForm input[name='adjustNumber']").val();
	                param['remark'] = $(".adjustPanelForm textarea[name='remark']").val();
	                $(".adjustPanelForm input:radio[name='adjustRadio']").each(function () { 
	             		 	if(this.checked){
	                 			param['adjustRadio'] = this.value; //获取单选框的值：是否门型设备账号
	             		 	}
	        	    })
	                if(!param['orgId']){
                        layer.msg('仓库不能为空！');
    	        	    return;
	                }
	                if(!param['containerCode']){
                        layer.msg('器具代码不能为空！');
    	        	    return;
	                }
	                if(param['adjustNumber'] > 100000000){
                        layer.msg('调整数量过大，输入数量请不要超过一亿！');
                        return;
					}
	        	    var reg = /^[0-9]*$/;
	                if(!(param['adjustNumber'] && reg.test(param['adjustNumber']) )){
                        layer.msg('加减库存不能为空且必须是正整数');
    	        	    return;
	                }
	                if(!param['remark']){
                        layer.msg('备注不能为空！');
    	        	    return;
	                }
	          	    layer.load(2); //加载中loading效果
	                $.ajax({
	                    url: basePath + "/inventory/adjustNumber",
	                    type: "POST",
	                    dataType: "json",
	                    data: param,
	                    success: function (result, textStatus, jqXHR) {
	                        layer.closeAll('loading'); //关闭加载中loading效果
	                        layer.msg(result.msg);
	                        if (result.status == 200) {
	                            layer.closeAll('page');   //成功后，关闭所有弹出框
	                            layui.$('.layui-search-form .queryButton').click(); //重新加载table数据
	                        }
					  	 },error: function(index, upload){
				            	layer.closeAll('loading'); //关闭加载中loading效果
				         }
	                });
	            }
	        });
	    });
});
</script>
</html>