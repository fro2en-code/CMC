<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>工种管理</title>
<style type="text/css">
.menuTable .menulevel2 {
	padding: 5px 0px 5px 50px;
}
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="layui-form layui-search-form myLabelWidth45">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">仓库</label>
				<div class="layui-input-inline">
					<select name="orgSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">工种</label>
				<div class="layui-input-inline">
					<input type="text" name="jobName" autocomplete="off" class="layui-input">
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="61"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm addButton" data-type="">新增</button>
				</cmc:button>
			</div>
		</div>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
		
		<script type="text/html" id="handleBar">
				<cmc:button buttonId="62">
  					<a class="layui-btn layui-btn-xs" lay-event="editName">编辑名称</a>
				</cmc:button>
				<cmc:button buttonId="63">
 					<a class="layui-btn layui-btn-xs" lay-event="editMenu">编辑工种菜单权限</a>
				</cmc:button>
		</script>

	<%-- 全选、全不选 --%>
	<div class="demoTable layui-form layui-search-form">
		<div class="layui-form-item">
			<div class="layui-inline" style="width: 350px;">
				<input type="radio" name="fullOrEmptySelect" value="1" title="全选" lay-filter="radioFilter" />  
				<input type="radio" name="fullOrEmptySelect" value="0" title="全不选" lay-filter="radioFilter"  /> 
				<input type="radio" name="fullOrEmptySelect" value="2" title="反选" lay-filter="radioFilter"  /> 
			</div>
			<div class="layui-inline">
				<label class="layui-form-label"  style="width: 130px;">当前编辑工种:</label>
				<label class="layui-form-label"  style="width: 220px;text-align: center;font-weight: 900;"><div id="currentJobName"></div></label>
    			<input type="text" id="currentJobId" autocomplete="off" class="layui-input layui-hide">
    			
				<span style="padding-left: 100px;">
    			<button class="layui-btn saveButton" lay-submit="" lay-filter="demo2">保存新权限</button>
    			</span>
			</div>
		</div>
	</div>
		
	<!-- 菜单列表 -->	
	<table class="menuTable layui-table layui-form" style=""  lay-filter="myLayFilter2">
			<colgroup>
				<col width="250">
				<col>
			</colgroup>
			<thead>
				<tr>
					<th style="text-align: center;">菜单名称</th>
					<th style="text-align: center;">页面按钮</th>
				</tr>
			</thead>
			<tbody id="myTableBody"></tbody>
	</table>

		<div class="layui-form demoTable"
			style="text-align: center; margin: 20px 0px 30px 0px;">
			<div class="layui-form-item">
				<button class="layui-btn saveButton" lay-submit="" lay-filter="demo2">保存新权限</button>
			</div>
		</div>

		<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>
<!-- 工种名称编辑 -->
<script type="text/html" id="editNamePanel">
<form class="layui-form layerForm editNamePanelForm myLabelWidth105" action="">
<div class="layui-form-item">
  <label class="layui-form-label">工种名称<span class="myRedColor">*</span></label>
  <div class="layui-input-block">
    <input type="text" name="jobName" lay-verify="required" placeholder="此项为必填" autocomplete="off" class="layui-input">
  </div>
</div>
</form>
</script>

<script type="text/javascript">
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element','form','layedit'], function(){
  var table = layui.table;
  var layer = layui.layer;
  var form = layui.form;
  var element = layui.element;
  initLeftmenu(element); //初始化左边菜单导航栏
  
 //时间选择器
 layui.laydate.render({
    elem: '#startDate'
    ,type: 'datetime'
    ,min: '2018-01-01'
  });
 //时间选择器
 layui.laydate.render({
    elem: '#endDate'
    ,type: 'datetime'
    ,min: '2018-01-01'
  });
//重置按钮
 layui.$('.resetButton').on('click', function(){
 	  $(".layui-search-form input[name='jobName']").val("");
 	  $(".layui-search-form select[name='orgSelect']").val("");
 		form.render();
 });
//初始化隶属公司下拉选择框
 $.ajax({
 	  url: basePath + "/org/filialeSystemOrgList",
 	  type:"POST",
 	  dataType: "json",
 	  data: {},
 	  success: function(result, textStatus, jqXHR){
 		    var options = "<option value=\"\">直接选择或搜索选择</option>";
 		    var list = result.list;
	  		$.each( list, function(i, obj){
	  		  	options += "<option value=\""+obj.orgId+"\">"+obj.orgName+"</option>";
	  		});
	  		$("select[name='orgSelect']").html(options);
	  		form.render();
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
    ,url:  basePath + '/job/pagingJob' //数据接口
    ,cols: [[
         {field: 'orgName', title: '仓库', width:250, sort: false}
        ,{field: 'jobName', title: '工种', width:180, sort: false}
        ,{field: 'createTime', title: '创建人与创建时间', sort: false,templet: function(obj){
        	return '<span class="tableCellPre">'+ obj.createRealName +'</span>'
        				+'<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>'
        }}
        ,{field: 'modifyTime', title: '修改人与修改时间', sort: false,templet: function(obj){
    	  		if(obj.modifyRealName && obj.modifyTime){
    		      	return '<span class="tableCellPre">'+ obj.modifyRealName +'</span>'
    						+'<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.modifyTime) +'</span>'
    	  		}
    	  		return "";
		}}
      ,{ title: '操作', width:250, sort: false, align:'center', toolbar: '#handleBar'} 
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
	        ,limit: 5  //每页多少条
	        ,limits: [5, 10, 50] //支持每页数据条数选择
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
            	 'jobName':$(".layui-search-form input[name='jobName']").val(),
                 'selectOrgId':$("select[name='orgSelect']").val()
             }
      };
    	if(currValue){
    		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
    	}
  	table.reload('layuiReloadId', reloadParam);
  }
  //点击查询按钮
  layui.$('.queryButton').on('click', function(){
	  reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
  });
  
  //监听工具条
  table.on('tool(myLayFilter)', function(obj){
	    var data = obj.data;
	    if(obj.event === 'editName'){  //点击“编辑名称”按钮
	    	editJobNameFunction(data);
	    } else if(obj.event === 'editMenu'){  //点击“编辑工种菜单权限”按钮
	    	editJobMenuFunction(data);
	    }
  }); 
  //点击“编辑工种菜单权限”按钮
  var editJobMenuFunction = function(data){
	  $("#currentJobId").val(data.jobId);
	  $("#currentJobName").html(data.jobName);
	  //获取指定仓库指定工种的权限菜单
	  layer.load(2); //加载中loading效果
	  $.ajax({
	  	  url: basePath + "/menuweb/queryMenuByJob",
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {"jobId" : data.jobId},
	  	  success: function(result, textStatus, jqXHR){
	        	layer.closeAll('loading'); //关闭加载中loading效果
  			  //清空之前勾选
	  		  $("input:checkbox[name='menuCheckBox']").each(function () { 
	  			  this.checked = false;
	          }) 
	  		  //选中该工种权限菜单
	  		  $("input:checkbox[name='menuCheckBox']").each(function () { 
	  			  var checkboxMenuId = this.value;
	  			  var checkbox = this;
		          $.each(result.list, function(i, obj){
					   if(checkboxMenuId == obj.menuId){  //匹配该工种是否有此菜单权限
						   checkbox.checked = true;  //有权限就勾选
					   }
				  });
	          }) 
	          form.render();
		  },error: function(index, upload){
	        	layer.closeAll('loading'); //关闭加载中loading效果
	      }
	  });
	  //获取指定仓库指定工种的权限按钮
	  $.ajax({
	  	  url: basePath + "/button/queryButtonByJob",
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {"jobId" : data.jobId},
	  	  success: function(result, textStatus, jqXHR){
  			  //清空之前勾选
	  		  $("input:checkbox[name='buttonCheckBox']").each(function () { 
	  			  this.checked = false;
	          }) 
	  		  //选中该工种权限按钮
	  		  $("input:checkbox[name='buttonCheckBox']").each(function () {
	  			  var checkboxButtonId = this.value;
	  			  var checkbox = this;
		          $.each(result.list, function(i, obj){
					   if(checkboxButtonId == obj.buttonId){  //匹配该工种是否有此按钮权限
						   checkbox.checked = true;  //有权限就勾选
					   }
				  });
	          }) 
	          form.render();
	  	  }
	  });
  }
  //点击“编辑名称”按钮
  var editJobNameFunction = function(data){
		  	var layerEditName = layer.open({
		  	    type: 1 //Page层类型
		  	    ,area: ['500px', '200px']
		  	    ,title: '编辑工种名称'
		  	    ,shade: 0.6 //遮罩透明度
		  	    ,maxmin: true //允许全屏最小化
		  	    ,anim: -1 //0-6的动画形式，-1不开启
		  	    ,content: $("#editNamePanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
		  		,cancel: function(){
		  			//alert("关闭啦");
			  	 }
		  	    ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
		  	    	$(".editNamePanelForm input[name='jobName']").val(data.jobName);
	            }  
		  	    ,btn: ['保存']
		  	    ,btn1: function(index){
		  	    	var param = {"jobName":$(".editNamePanelForm input[name='jobName']").val(),"jobId":data.jobId};
		  	    	layer.load(2); //加载中loading效果
		  	    	$.ajax({
				      		  url: basePath + "/job/editJobName", //"编辑名称"按钮
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
  //新增按钮点击
  layui.$('.addButton').on('click', function(){
	  	var layerEditName = layer.open({
	  	    type: 1 //Page层类型
	  	    ,area: ['500px', '200px']
	  	    ,title: '新增工种'
	  	    ,shade: 0.6 //遮罩透明度
	  	    ,maxmin: true //允许全屏最小化
	  	    ,anim: -1 //0-6的动画形式，-1不开启
	  	    ,content: $("#editNamePanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
	  		,cancel: function(){
	  			//alert("关闭啦");
		  	 }
	  	    ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
		  		
          	}  
	  	    ,btn: ['保存']
	  	    ,btn1: function(index){
	  	    	var param = {"jobName":$(".editNamePanelForm input[name='jobName']").val()};
	  	    	layer.load(2); //加载中loading效果
	  	    	$.ajax({
		      		  url: basePath + "/job/addJob",
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
  
  
  
  
  /**************************************************************************************
  							以下是菜单表格相关事件
  **************************************************************************************/
//拿到所有按钮列表数据
  $.ajax({
  	  url: basePath + "/button/listAllButton",
  	  type:"POST",
  	  dataType: "json",
  	  data: {},
  	  success: function(result, textStatus, jqXHR){
  			allButtonList = result.list; 
  			fillTableData(); //加载完毕按钮列表后，才加载菜单列表，否则会有延迟加载按钮列表现象，导致按钮列空白
  	  }
  });
  
  var fillTableData = function(){
	  //拿到所有菜单数据，并渲染菜单数据表格
	  $.ajax({
	  	  url: basePath + "/menuweb/listAllMenu", 
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {},
	  	  success: function(result, textStatus, jqXHR){
	  			var list = result.list;
	  			var tbodyHtml = "";
	  			$.each( list, function(i, obj){
		  			tbodyHtml += "<tr>";
		  			//第一个单元格
		  			var menuLevelCss = "";
		  			if(obj.menuLevel == 2){
		  				menuLevelCss = "menulevel2";
		  			}
		  			tbodyHtml += "<td class='"+menuLevelCss+"'>";
		  			tbodyHtml += "<input type='checkbox' name='menuCheckBox' lay-filter='menuCheckboxFilter' lay-skin='primary' parentMenuId='"
	  					+(obj.parentMenuId?obj.parentMenuId:'')+"' menuId='"+obj.menuId+"' menuLevel="+obj.menuLevel+" value='"+obj.menuId+"'  title='"+obj.menuName+"' >";
		  			
		  			//第二个单元格
		  			tbodyHtml += "<td>";
		  			$.each( allButtonList, function(ii, button){
		  				 if(button.menuId == obj.menuId){
				  			tbodyHtml += "<input type='checkbox' name='buttonCheckBox' lay-filter='buttonCheckboxFilter' lay-skin='primary' parentMenuId='"
			  					+(obj.parentMenuId?obj.parentMenuId:'')+"' menuId='"+button.menuId+"' menuLevel="+obj.menuLevel
			  					+" value='"+button.buttonId+"'  title='"+button.buttonName + "' >"; 
		  				 }
		  			});
		  			tbodyHtml +="</td>";
		  			tbodyHtml += "</tr>";
		  		});
		  		$("#myTableBody").html(tbodyHtml); //初始化渲染数据表格
		  		//监听菜单复选框点击事件
				$("input:checkbox[name='menuCheckBox']").click(function(){
					this.checked = true;  
				});
		  		//监听按钮复选框点击事件
				$("input:checkbox[name='buttonCheckBox']").click(function(){
					this.checked = true;  
				});

		  		//初始化表格里的form元素
		  		form.render();
		  		//初始化layui静态表格
		  		table.render();
		  		monitorMenuCheckboxClick();
	  	  }
	  });
  }
  /**
	 一二级菜单选择联动：
	 1，勾选某一级菜单时，全选其二级菜单。以及勾选这些二级菜单的页面按钮。
	 2，反勾选某二级菜单时，反勾选其一级菜单。
	*/
	var monitorMenuCheckboxClick = function(){
		  form.on('checkbox(menuCheckboxFilter)', function(data){
			  var menuId = $(this).val();
		      var currentChecked = this.checked;
			  //如果点击的是一级菜单
			  if($(this).attr("menuLevel") == 1){
				    $("input:checkbox[name='menuCheckBox'],input:checkbox[name='buttonCheckBox']").each(function (i,n) { 
				        if($(this).attr("parentMenuId") == menuId){
				        	this.checked = currentChecked;  
						}
				   });
			  }
			  //如果点击的是二级菜单
			  if($(this).attr("menuLevel") == 2){
				  $("input:checkbox[name='buttonCheckBox']").each(function (i,n) { 
				        if($(this).attr("menuId") == menuId){
				        	this.checked = currentChecked;  
						}
				   });
				  if(currentChecked){ //如果二级菜单被勾选，则必须勾选其一级菜单
				      var currentParentMenuId = $(this).attr("parentMenuId");
					    $("input:checkbox[name='menuCheckBox']").each(function (i,n) {
					        if($(this).attr("menuId") == currentParentMenuId){
					        	this.checked = currentChecked;  
							}
					   });
				  }
			  }
			  form.render();
		  });
	}
  
  //监听全选、全不选单选框点击事件，动态更新checked属性
  form.on('radio(radioFilter)', function(data){
  	  var currentValue = data.value;
	  	if(currentValue == 1){ //全选
	  		$("input:checkbox[name='menuCheckBox']").each(function () { 
	            this.checked = true;  
	         }) 
			$("input:checkbox[name='buttonCheckBox']").each(function () { 
	            this.checked = true;  
	         }) 
	  	}
	  	if(currentValue == 0){ //全不选
			$("input:checkbox[name='menuCheckBox']").each(function () { 
				this.checked = false;
	         }) 
			$("input:checkbox[name='buttonCheckBox']").each(function () { 
				this.checked = false;
	         }) 
		}
	  	if(currentValue == 2){ //反选
			$("input:checkbox[name='menuCheckBox']").each(function () { 
				this.checked = !this.checked;
	         }) 
			$("input:checkbox[name='buttonCheckBox']").each(function () { 
				this.checked = !this.checked;
	         }) 
		}
	  	form.render();
  });  

  //点击“保存新权限”按钮
  layui.$('.demoTable .saveButton').on('click', function(){
	  	  var currentJobId = $("#currentJobId").val();
	  	  if(!currentJobId){
	  			layer.msg("请先选择一个需要编辑菜单权限的工种！");
	  			return;
	  	  }
	  	  var param = {"jobId":currentJobId};
	  	  //菜单选中值
	  	   var menuArr = new Array();
		  	$("input:checkbox[name='menuCheckBox']").each(function () {
	            if(this.checked) {
	            	menuArr.push($(this).attr("value"));
	            }
	        });
		  	param["menuList"] = menuArr;
		  	//按钮选中值
	  	    var buttonArr = new Array();
		  	$("input:checkbox[name='buttonCheckBox']").each(function () {
	            if(this.checked) {
	            	buttonArr.push($(this).attr("value"));
	            }
	        });
		  	param["buttonList"] = buttonArr;
	  	  //加载当前所选账号的权限菜单和权限按钮
     	  layer.load(2); //加载中loading效果
		  $.ajax({
		  	  url: basePath + "/job/saveJobMenuWeb",  //“保存新权限”按钮
		  	  type:"POST",
		  	  dataType: "json",
		  	  data: param,
		  	  success: function(result, textStatus, jqXHR){
		  		   layer.closeAll('loading'); //关闭加载中loading效果
		  		  	if(result.status == 200){
		  		  		layer.msg("保存成功！");
		  		  	}else{
		  		  		layer.msg(msg);
		  		  	}	  		    
		  	  },error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	          }
		  });
  }); 
});
</script>
</html>