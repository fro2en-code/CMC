<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>组织机构管理</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>

	<div class="demoTable layui-form layui-search-form">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">组织名称</label>
				<div class="layui-input-inline">
					<input type="text" name="orgName" autocomplete="off" class="layui-input">
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton"
					data-type="reload">查询</button>
					<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
			</div>
			<div class="layui-inline" style="margin-left: 15px;">
			    <cmc:button buttonId="72"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm addButton" data-type="reload">新增</button>
				</cmc:button>
			</div>
			<div class="layui-inline">
                <cmc:button buttonId="73"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm" onclick="downloadTemplate('/org/orgExcelDownload')">导入模版下载</button>
				</cmc:button>
			</div>
			<div class="layui-inline">
					<cmc:button buttonId="74"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button type="button" class="layui-btn layui-btn-sm " id="uploadExcel">
						<i class="layui-icon"></i>批量导入
						</button>
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
<!-- “编辑机构”弹出框 -->
<script type="text/html" id="editOrgPanel">
<div class="layerForm editOrgPanelForm myLabelWidth120" >
    <form class="layui-form" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">上级组织<span class="myRedColor">*</span></label>
            <div class="layui-input-inline" style="width:470px;">
                	<select name="parentOrgId"  lay-search="">
                    	<option value="">直接选择或搜索选择</option>
                	</select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">组织名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="orgName" autocomplete="off" class="layui-input" style="width:470px;">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">组织代码<span class="myRedColor">*</span></label>
            <div class="layui-input-block">
            	<div class="layui-input-inline">
                	<input type="text" name="orgCode" autocomplete="off" placeholder="请输入2到7位大写字母" class="layui-input">
            	</div>
            	<label class="layui-form-label">组织类型<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
                	<select name="orgTypeId" >
                    	<option value="">请选择</option>
                	</select>
            	</div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">联系人姓名</label>
            <div class="layui-input-block">
            	<div class="layui-input-inline">
                	<input type="text" name="contactName" autocomplete="off" class="layui-input">
            	</div>
            	<label class="layui-form-label">联系人电话</label>
            	<div class="layui-input-inline">
                	<input type="text" name="contactPhone" autocomplete="off"  class="layui-input">
            	</div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">组织地址</label>
            <div class="layui-input-inline">
                <input type="text" name="orgAddress" autocomplete="off" class="layui-input" style="width:470px;">
            </div>
        </div>
    </form>
</div>
</script>
<!-- “新增机构”弹出框 -->
<script type="text/html" id="ddOrgPanel">
<div class="layerForm ddOrgPanelForm" >
    <form class="layui-form myLabelWidth120" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">上级组织<span class="myRedColor">*</span></label>
            <div class="layui-input-inline" style="width:470px;">
                	<select name="parentOrgId"  lay-search="">
                    	<option value="">直接选择或搜索选择</option>
                	</select>
            </div>
        </div>
        <div class="layui-form-item">
			<div class="layui-inline">
            	<label class="layui-form-label">组织名称<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
                	<input type="text" name="orgName" autocomplete="off" class="layui-input" style="width:470px;">
            	</div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">组织代码<span class="myRedColor">*</span></label>
            <div class="layui-input-block">
            	<div class="layui-input-inline">
                	<input type="text" name="orgCode" autocomplete="off" placeholder="请输入2到7位大写字母" class="layui-input">
            	</div>
            	<label class="layui-form-label">组织类型<span class="myRedColor">*</span></label>
            	<div class="layui-input-inline">
                	<select name="orgTypeId" >
                    	<option value="">请选择</option>
                	</select>
            	</div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">联系人姓名</label>
            <div class="layui-input-block">
            	<div class="layui-input-inline">
                	<input type="text" name="contactName" autocomplete="off" class="layui-input">
            	</div>
            	<label class="layui-form-label">联系人电话</label>
            	<div class="layui-input-inline">
                	<input type="text" name="contactPhone" autocomplete="off"  class="layui-input">
            	</div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">组织地址</label>
            <div class="layui-input-inline">
                <input type="text" name="orgAddress" autocomplete="off" class="layui-input" style="width:470px;">
            </div>
        </div>
    </form>
</div>
</script>


<!--例子代码，勿删！  
<div>
	<span style="padding:9px 15px;width:160px;text-align: right;width:110px;display:-moz-inline-box;display:inline-block;">上级组织</span>
	<select name="parentOrgId" lay-ignore class="multiselect">
	    <option value="">请选择</option>
	</select>
</div> -->

<script type="text/javascript">
layui.use(['laydate', 'layer', 'table', 'element','form','layedit', 'upload'], function(){
	  var table = layui.table;
	  var layer = layui.layer;
	  var form = layui.form;
	  var layedit = layui.layedit;
      var upload = layui.upload;
      var element = layui.element;
      initLeftmenu(element); //初始化左边菜单导航栏
	  
	  var orgList = []; //当前选中仓库的所有子公司列表，包括自己
	  //初始化隶属公司下拉选择框
	  $.ajax({
	  	  url: basePath + "/org/listAllOrg",
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
		  		$("select[name='orgSelect']").html(options);
		  		form.render();
	  	  }
	  });
	  var orgTypeList = [];//初始化组织类型
	  //初始化隶属公司下拉选择框
	  $.ajax({
	  	  url: basePath + "/org/listAllOrgType",
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {},
	  	  success: function(result, textStatus, jqXHR){
			  	var options = "<option value=\"\">直接选择或搜索选择</option>";
	  		    var list = result.list;
	  		    orgTypeList = list; //赋值给全局变量
	  	  }
	  });
	  var parentList = [];
    //初始化上级公司下拉选择框
    $.ajax({
        url: basePath + "/org/findParent",
        type:"POST",
        dataType: "json",
        data: {},
        success: function(result, textStatus, jqXHR){
            var options = "<option value=\"\">直接选择或搜索选择</option>";
            var list = result.list;
            parentList = list; //赋值给全局变量
        }
    });


      //批量导入
      upload.render({
          elem: '#uploadExcel'
          ,url: basePath+'/org/batchUpload'
          ,accept: 'file' //普通文件
          ,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
          	layer.load(2); //加载中loading效果
          }
          ,done: function(result){
          	layer.closeAll('loading'); //关闭加载中loading效果
              if(result.status == 200){
              		layer.msg('批量导入成功！'); 
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

	  layer.load(2); //加载中loading效果
	  //方法级渲染
	  table.render({
	    elem: '#LAY_TABLE'
	    ,loading:true 
	    ,done: function(res, curr, count){
        	layer.closeAll('loading'); //关闭加载中loading效果
	    }
	    ,url:  basePath + '/org/pagingSystemOrg' //数据接口
	    ,cols: [[
	      {field: 'orgName', title: '组织名称', width:260, sort: false} 
	      ,{field: 'orgTypeName', title: '组织类型', width:120, sort: false}
	      ,{field: 'orgCode', title: '组织代码', width:120, sort: false}
	      ,{field: 'parentOrgName', title: '上级组织名称', width:120, sort: false}
	      ,{field: 'contactName', title: '联系人姓名', width:100, sort: false}
	      ,{field: 'contactPhone', title: '联系电话', width:100, sort: false}
	      ,{field: 'orgAddress', title: '地址', width:150, sort: false}
	      ,{field: 'isActive', title: '组织状态', width:100, sort: false,templet: function(obj){
	  	  		var result = '';
	  	  		var disabledCss = ' class="layui-disabled" ';
	  	  		var disabledStr = ' disabled="disabled" ';
	  	  		
	  	  		<cmc:button buttonId="75">
		  			disabledCss = '';
		  			disabledStr = '';
	  	  		</cmc:button>
	  	  	
		  		if(obj.orgId == '100'){ //总公司不允许编辑
		  			disabledCss = ' class="layui-disabled" ';
		  			disabledStr = ' disabled="disabled" ';
		  		}
	  			result = '<input type="checkbox" orgId="'+obj.orgId+'" value='+obj.isActive+' name="ifKey" lay-skin="switch" lay-filter="isActiveSwitch" lay-text="启用|禁用" '
			         +(obj.isActive == '0' ? 'checked' : '')+'  '+disabledCss + disabledStr+'  >';
		  		return result;
		  }}
	      ,{ title: '操作', width:70, sort: false, align:'center',templet: function(obj){
		  		if(!(obj.orgId == '100')){ //总公司不能进行编辑
					var result = '';
		  			<cmc:button buttonId="76">
		  			result += '<a class="layui-btn layui-btn-xs" lay-event="editOrg">编辑</a>';
		  			</cmc:button>
					return result;
		  		}
		  		return "";
			}}
	      ,{field: 'createTime', title: '创建人与创建时间', width:250, sort: false,templet: function(obj){
	  	  		var result = "";
		  		if(obj.createRealName){
		  			result += '<span class="tableCellPre">'+ obj.createRealName +'</span>';
		  		}
		  		if(obj.createTime){
		  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
		  		}
		  		return result;
			}}
		    ,{field: 'modifyTime', title: '修改人与修改时间', width:250, sort: false,templet: function(obj){
		  	  		var result = "";
		  	  		if(obj.modifyRealName){
		  	  			result += '<span class="tableCellPre">'+ obj.modifyRealName +'</span>';
		  	  		}
		  	  		if(obj.modifyTime){
		  	  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.modifyTime) +'</span>';
		  	  		}
		  	  		return result;
			}}
		    ,{field: 'orgId', title: '组织ID', width:290, sort: false}
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
      var reloadTableData = function(currValue){
    	    layer.load(2); //加载中loading效果
      	  var reloadParam = {
                 where: {
                	 'orgName':$(".layui-search-form input[name='orgName']").val()
                 }
          };
        	if(currValue){
        		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
        	}
      	table.reload('layuiReloadId', reloadParam);
      }
	  //点击查询按钮
	  layui.$('.demoTable .queryButton').on('click', function(){
		  reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
	  });

	  //重置按钮
      layui.$('.resetButton').on('click', function(){
      	  $(".layui-search-form input[name='orgName']").val("");
      });
      //监听表格工具条：编辑、修改密码、禁用
      table.on('tool(myLayFilter)', function(obj){
          var data = obj.data;
          if(obj.event === 'editOrg'){
        	  editOrg(data);
          }
      });
      //“组织状态”列，启用、禁用按钮点击事件
   	 form.on('switch(isActiveSwitch)', function(data){ 
  		  var param = {};
  		  param['orgId'] = $(data.elem).attr('orgId');
 		  if(data.elem.checked){ //如果开启就是启用
 			  param['isActive'] = 0 ;
 		  }else{
 			  param['isActive'] = 1 ;
 		  }
 		  layer.load(2); //加载中loading效果
          $.ajax({
              url: basePath + "/org/changeActiveOrg",
              type:"POST",
              dataType: "json",
              data: param,
              success: function(result, textStatus, jqXHR){
	            	layer.closeAll('loading'); //关闭加载中loading效果
                    layer.msg(result.msg);
                    if(result.status == 200){
                    	reloadTableData(); //重新加载table数据
                    }
              },error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	          }
          });                                                                         
 	 }); 
      //点击表格的“编辑”按钮事件
      var editOrg = function(data){
          layer.open({
              type: 1 //Page层类型
              ,area: ['700px', '520px']
              ,title: '编辑机构'
              ,shade: 0.6 //遮罩透明度
              ,maxmin: true //允许全屏最小化
              ,anim: -1 //0-6的动画形式，-1不开启
              ,content: $("#editOrgPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
              ,cancel: function(){
                  //alert("关闭啦");
              }
              ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
            	  //设置弹出框form表单的值
            	  $(".editOrgPanelForm input[name='orgName']").val(data.orgName);
            	  $(".editOrgPanelForm input[name='orgCode']").val(data.orgCode);
            	  $(".editOrgPanelForm input[name='orgAddress']").val(data.orgAddress);
            	  $(".editOrgPanelForm input[name='contactName']").val(data.contactName);
            	  $(".editOrgPanelForm input[name='contactPhone']").val(data.contactPhone);


                  var orgOptions = '<option value=\'\'>直接选择或搜索选择</option>';
		          $.each(orgList, function(i, obj){
		        	  orgOptions += "<option value='"+obj.orgId+"'>"+obj.orgName+"</option>";
				  });
		          $(".editOrgPanelForm select[name='parentOrgId']").html(orgOptions);
            	  $(".editOrgPanelForm select[name='parentOrgId']").val(data.parentOrgId);

            	  //初始化“组织类型”下拉框
            	  var options = "<option value=''>直接选择或搜索选择</option>";
	  		  	  $.each( orgTypeList, function(i, obj){
	  		  		  	options += "<option value=\""+obj.typeId+"\">"+obj.typeName+"</option>";
	  		  	  });
	  		  	  $(".editOrgPanelForm select[name='orgTypeId']").html(options);
            	  $(".editOrgPanelForm select[name='orgTypeId']").val(data.orgTypeId);
                  form.render();
              }
              ,btn: ['保存']
              ,btn1: function(index){
	                  var param = {};
	                  param['orgId'] = data.orgId;
	                  param['orgName'] = $(".editOrgPanelForm input[name='orgName']").val();
	                  param['orgCode'] = $(".editOrgPanelForm input[name='orgCode']").val();
	                  param['orgAddress'] = $(".editOrgPanelForm input[name='orgAddress']").val();
	                  param['contactName'] = $(".editOrgPanelForm input[name='contactName']").val();
	                  param['contactPhone'] = $(".editOrgPanelForm input[name='contactPhone']").val();
	                  param['parentOrgId'] = $(".editOrgPanelForm select[name='parentOrgId']").val();
	                  param['orgTypeId'] = $(".editOrgPanelForm select[name='orgTypeId']").val();
	                  if(!param['parentOrgId']){
	                	  layer.msg("上级组织不能为空！");
	                	  return;
	                  }
	                  var parentOrgName = $(".editOrgPanelForm select[name='parentOrgId'] option:selected").text();
				      if (parentOrgName == param['orgName']){
                          layer.msg("上级组织不能和组织名称相同");
                          return;
					  }

	                  layer.load(2); //加载中loading效果
	                  $.ajax({
			      		  url: basePath + "/org/editSystemOrg",
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

	  //点击"新增"按钮
	  layui.$('.demoTable .addButton').on('click', function(){
          layerEditName = layer.open({
              type: 1 //Page层类型
              ,area: ['800px', '550px']
              ,title: '新增机构'
              ,shade: 0.6 //遮罩透明度
              ,maxmin: true //允许全屏最小化
              ,anim: -1 //0-6的动画形式，-1不开启
              ,content: $("#ddOrgPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
              ,cancel: function(){
                  //alert("关闭啦");
              }
              ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
	            	  //设置弹出框form表单的值
	            	  $(".ddOrgPanelForm input[name='orgName']").val('');
	            	  $(".ddOrgPanelForm input[name='orgCode']").val('');
	            	  $(".ddOrgPanelForm input[name='orgAddress']").val('');
	            	  $(".ddOrgPanelForm input[name='contactName']").val('');
	            	  $(".ddOrgPanelForm input[name='contactPhone']").val('');
	            	  //初始化“组织类型”下拉框
	            	  var options = "<option value=''>直接选择或搜索选择</option>";
		  		  	  $.each( orgTypeList, function(i, obj){
		  		  		  	options += "<option value=\""+obj.typeId+"\">"+obj.typeName+"</option>";
		  		  	  });
		  		  	  $(".ddOrgPanelForm select[name='orgTypeId']").html(options);
	                  form.render();
	                  
	                  //初始化弹出框里下拉框的option选项值——隶属机构
	                  var orgOptions = "<option value=''>直接选择或搜索选择</option>";
	 		          $.each(orgList, function(i, obj){
	 		        	 orgOptions += "<option value='"+obj.orgId+"'>"+obj.orgName+"</option>";
	 				  });
	 		          $(".ddOrgPanelForm select[name='parentOrgId']").html(orgOptions); 
	                  form.render();
              }
              ,btn: ['保存']
              ,btn1: function(index){
	                  var param = {};
	                  param['orgName'] = $(".ddOrgPanelForm input[name='orgName']").val();
	                  param['orgCode'] = $(".ddOrgPanelForm input[name='orgCode']").val();
	                  param['orgAddress'] = $(".ddOrgPanelForm input[name='orgAddress']").val();
	                  param['contactName'] = $(".ddOrgPanelForm input[name='contactName']").val();
	                  param['contactPhone'] = $(".ddOrgPanelForm input[name='contactPhone']").val();
	                  param['orgTypeId'] = $(".ddOrgPanelForm select[name='orgTypeId']").val();
	                  param['parentOrgId'] = $(".ddOrgPanelForm select[name='parentOrgId']").val();
	                  if(!param['parentOrgId']){
	                	  layer.msg("上级组织不能为空！");
	                	  return;
	                  }
					  var parentOrgName = $(".ddOrgPanelForm select[name='parentOrgId'] option:selected").text();
					  if (parentOrgName == param['orgName']){
						  layer.msg("上级组织不能和组织名称相同");
						  return;
					  }
	                  layer.load(2); //加载中loading效果
	                  $.ajax({
			      		  url: basePath + "/org/addSystemOrg",
			      		  type:"POST",
			      		  dataType: "json",
			      		  data: param,
			      		  success: function(result, textStatus, jqXHR){
			            	  layer.closeAll('loading'); //关闭加载中loading效果
			      			  layer.msg(result.msg);
				  			  if(result.status == 200){
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
});
</script>
</html>