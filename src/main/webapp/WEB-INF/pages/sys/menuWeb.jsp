<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>用户web端权限管理</title>
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
	<div class="layui-form demoTable layui-search-form">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:95px;">登录名账号</label>
				<div class="layui-input-inline">
					<select name="accountSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryMemberOrgButton">获取账号隶属仓库</button>
			</div>
			<div class="layui-inline" >
				 <div class="layui-input-inline" >
						<select name="orgSelect" lay-search="" >
							<option value="">直接选择或搜索仓库</option>
						</select>
				 </div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
			</div>
		</div>
	<div class="layui-form demoTable layui-search-form">
		<div class="layui-form-item">
			<label class="layui-form-label" style="width:120px;"><b>选中账号：</b></label>
			<label class="layui-form-label" style="width:200px;text-align: center;"><span id="currentSelectAccount"></span></label>
				<label class="layui-form-label"><b>工种:</b></label>
				<div class="layui-input-block">
				 	<div class="layui-input-block" >
						<div id="allJobs"></div>
					</div>
				</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline" style="width: 900px;">
				<input type="radio" name="fullOrEmptySelect" value="1" title="全选" lay-filter="radioFilter" />  
				<input type="radio" name="fullOrEmptySelect" value="0" title="全不选" lay-filter="radioFilter"  /> 
				<input type="radio" name="fullOrEmptySelect" value="2" title="反选" lay-filter="radioFilter"  /> 
				
				<span style="padding-left: 200px;">
				<cmc:button buttonId="70"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn saveButton" lay-submit="" lay-filter="demo2">保存新权限</button>
				</cmc:button>
				</span>
			</div>
		</div>
	</div>

	<table class="menuTable layui-table layui-form" style=""  lay-filter="myLayFilter">
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
			<cmc:button buttonId="80"> <!-- 用户有该按钮权限才会显示以下代码 -->
				<button class="layui-btn saveButton" lay-submit="" lay-filter="demo2">保存新权限</button>
			</cmc:button>
		</div>
	</div>

	<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>
<script type="text/javascript">
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element','form','layedit'], function(){
  var table = layui.table;
  var layer = layui.layer;
  var form = layui.form;
  var element = layui.element;
  initLeftmenu(element); //初始化左边菜单导航栏
  
  //自定义全局对象
  var allButtonList = [];
  
  //初始化登录名下拉选择框
  $.ajax({
  	  url: basePath + "/user/listAllUserByFiliale",
  	  type:"POST",
  	  dataType: "json",
  	  data: {},
  	  success: function(result, textStatus, jqXHR){
  		    var options = "<option value=\"\">搜索账号或姓名</option>";
  		    var list = result.list;
	  		$.each( list, function(i, obj){
	  		  	options += "<option value=\""+obj.account+"\">"+obj.account+" ("+obj.realName+")</option>";
	  		});
	  		$("select[name='accountSelect']").html(options);
	  		form.render();
  	  }
  });
  
  //监听工种复选框点击事件
  function jobCheckBoxClick(){
		  var selectMemberOrgId = $("select[name='orgSelect']").val();
	  	  if(!selectMemberOrgId){
	  		  layer.msg("必须选择一个隶属仓库");
	  		  return;
	  	  }
	    //监听工种复选框点击事件
		form.on('checkbox(jobCheckBoxLayFilter)', function(data){
	     	  layer.load(2); //加载中loading效果
			  //获取指定仓库指定工种的权限菜单
			  $.ajax({
			  	  url: basePath + "/menuweb/queryMenuByJob",
			  	  type:"POST",
			  	  dataType: "json",
			  	  data: {"jobId" : data.value,"memberOrgId":selectMemberOrgId},
			  	  success: function(result, textStatus, jqXHR){
			  		  $("input:checkbox[name='menuCheckBox']").each(function () { 
			  			  var checkboxMenuId = this.value;
			  			  var checkbox = this;
				          $.each(result.list, function(i, obj){
							   if(checkboxMenuId == obj.menuId){
								      if(data.elem.checked){ //如果选中工种复选框
								    	  checkbox.checked = true; 
							  		  }else{ //如果不选中工种复选框
							  			  checkbox.checked = false; 
							  		  }
							   }
						  });
			          }) 
					  form.render();
			  		  
			  		  //获取指定仓库指定工种的权限按钮
					  $.ajax({
					  	  url: basePath + "/button/queryButtonByJob",
					  	  type:"POST",
					  	  dataType: "json",
					  	  data: {"jobId" : data.value,"memberOrgId":selectMemberOrgId},
					  	  success: function(result, textStatus, jqXHR){
				            	layer.closeAll('loading'); //关闭加载中loading效果
					  		  	$("input:checkbox[name='buttonCheckBox']").each(function () {
						  			  var checkboxButtonId = this.value;
						  			  var checkbox = this;
							          $.each(result.list, function(i, obj){
										   if(checkboxButtonId == obj.buttonId){
											      if(data.elem.checked){ //如果选中工种复选框
											    	  checkbox.checked = true; 
										  		  }else{ //如果不选中工种复选框
										  			checkbox.checked = false; 
										  		  }
										   }
									  });
						          }) 
								  form.render();
					  	  },error: function(index, upload){
				            	layer.closeAll('loading'); //关闭加载中loading效果
				          }
					  });
			  		  
			  	  },error: function(index, upload){
		            	layer.closeAll('loading'); //关闭加载中loading效果
		          }
			  });
		}); 
  }
  
  //初始化所有按钮。拿到所有按钮列表数据
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
 	  layer.load(2); //加载中loading效果
	  $.ajax({
	  	  url: basePath + "/menuweb/listAllMenu", 
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {},
	  	  success: function(result, textStatus, jqXHR){
	        	layer.closeAll('loading'); //关闭加载中loading效果
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
		  					+(obj.parentMenuId?obj.parentMenuId:'')+"' menuId='"+obj.menuId+"' menuLevel="+obj.menuLevel
		  					+" value='"+obj.menuId+"'  title='"+obj.menuName+"' >";
		  			
		  			//第二个单元格
		  			tbodyHtml += "<td>";
		  			$.each( allButtonList, function(ii, button){
		  				 if(button.menuId == obj.menuId){
				  			tbodyHtml += "<input type='checkbox' name='buttonCheckBox' lay-filter='buttonCheckboxFilter' lay-skin='primary' parentMenuId='"
			  					+(obj.parentMenuId?obj.parentMenuId:'')+"' menuId='"+button.menuId+"' menuLevel="+obj.menuLevel
			  					+" value='"+button.buttonId+"' "
			  					//+"  title='"+button.buttonName + "' >";"
			  					+"  title='"+button.buttonName + "（"+button.buttonId+"）' >"; //显示按钮ID
		  				 }
		  			});
		  			tbodyHtml +="</td>";
		  			tbodyHtml += "</tr>";
		  		});
		  		$("#myTableBody").html(tbodyHtml); //初始化渲染数据表格

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
	         });
			$("input:checkbox[name='buttonCheckBox']").each(function () { 
	            this.checked = true;  
	         });
	  	}
	  	if(currentValue == 0){ //全不选
			$("input:checkbox[name='menuCheckBox']").each(function () { 
				this.checked = false;
	         }); 
			$("input:checkbox[name='buttonCheckBox']").each(function () { 
				this.checked = false;
	         }); 
		}
	  	if(currentValue == 2){ //反选
			$("input:checkbox[name='menuCheckBox']").each(function () { 
				this.checked = !this.checked;
	         }); 
			$("input:checkbox[name='buttonCheckBox']").each(function () { 
				this.checked = !this.checked;
	         }); 
		}
	  	form.render();
  });  
  //点击“获取账号隶属仓库”按钮
  layui.$('.demoTable .queryMemberOrgButton').on('click', function(){
	  	  var account = $("select[name='accountSelect']").val();
	  	  if(!account){
	  		  layer.msg("必须选择一个账号");
	  		  return;
	  	  } 
		  $("#currentSelectAccount").html(account);  //重新填充：选中账号
		  //清空隶属公司下拉框
		  var options = "<option value=\"\">直接选择或搜索仓库</option>";
  		  $("select[name='orgSelect']").html(options);

     	  layer.load(2); //加载中loading效果
	  	  //加载当前所选账号的权限菜单和权限按钮
		  $.ajax({
		  	  url: basePath + "/user/listFilterMemberOrg", 
		  	  type:"POST",
		  	  dataType: "json",
		  	  data: {'selectAccount':account},
		  	  success: function(result, textStatus, jqXHR){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	            	//针对admin账号的校验
	            	if(result.status != 200){
	            		layer.msg(result.msg);
	            		return;
	            	}
		  			var systemUser = result.bean;
		  			//初始化该账号隶属公司
		  			var memberOfOrgList = systemUser.memberOfOrgList;
		  		    var options = "<option value=\"\">直接选择或搜索仓库</option>";
			  		$.each( memberOfOrgList, function(i, obj){
			  		  	options += "<option value=\""+obj.orgId+"\">"+obj.orgName+"</option>";
			  		});
			  		$("select[name='orgSelect']").html(options);
	  				form.render(); //因为对复选框进行选中，所以要重新渲染表单
		  	  },error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	          }
		  });
  });
  
  //点击查询按钮
  layui.$('.demoTable .queryButton').on('click', function(){
	  	  var account = $("#currentSelectAccount").html();
	  	  if(!account){
	  		  layer.msg("必须选择一个账号");
	  		  return;
	  	  }
	  	  var selectMemberOrgId = $("select[name='orgSelect']").val();
	  	  if(!selectMemberOrgId){
	  		  layer.msg("必须选择一个隶属仓库");
	  		  return;
	  	  }
	  	  //加载当前所选账号的权限菜单和权限按钮
     	  layer.load(2); //加载中loading效果
		  $.ajax({
		  	  url: basePath + "/user/listAccountMenuWeb", 
		  	  type:"POST",
		  	  dataType: "json",
		  	  data: {'selectAccount':account,"selectMemberOrgId":selectMemberOrgId},
		  	  success: function(result, textStatus, jqXHR){
	            	layer.closeAll('loading'); //关闭加载中loading效果
		  			var systemUser = result.bean;
	            	//初始化账号的隶属工种
		  			var systemJobList = systemUser.systemJobList;
		  			var checkboxs = "";
			  		$.each( systemJobList, function(i, obj){
			  			checkboxs += '<input type="checkbox" name="jobCheckBox" lay-skin="primary" lay-filter="jobCheckBoxLayFilter" title="'+obj.jobName+'" value="'+obj.jobId+'" >';
			  		});
			  		$("#allJobs").html(checkboxs);
			  		
			  	     //监听工种复选框点击事件
			  		jobCheckBoxClick();
	  				//清空之前勾选的菜单
	  				$("input:checkbox[name='menuCheckBox']").each(function () { 
			            this.checked = false;  
			         }) 
		  			//把该账号拥有的权限菜单勾选上
		  			var systemMenuWebList = systemUser.systemMenuWebList;
		  			$.each( systemMenuWebList, function(i, obj){
		  				$("input:checkbox[name='menuCheckBox']").each(function () { 
		  					if(obj.menuId == this.value){
		  						this.checked = true;
			  				}
				         }) 
			  		});
		  			
	  				//清空之前勾选的按钮
	  				$("input:checkbox[name='buttonCheckBox']").each(function () { 
			            this.checked = false;  
			         }) 
		  			//把该账号拥有的权限按钮勾选上
		  			var systemButtonList = systemUser.systemButtonList;
		  			$.each( systemButtonList, function(i, obj){
		  				$("input:checkbox[name='buttonCheckBox']").each(function () { 
		  					if(obj.buttonId == this.value){
		  						this.checked = true;
			  				}
				         }) 
			  		});
	  				form.render(); //因为对复选框进行选中，所以要重新渲染表单
		  	  },error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	          }
		  });
  });
  //重置按钮
  $('.resetButton').on('click', function(){
  	  $(".layui-search-form select[name='accountSelect']").val("");
  	  $(".layui-search-form select[name='orgSelect']").val("");
  	  form.render();
  });
  //点击“保存新权限”按钮
  layui.$('.demoTable .saveButton').on('click', function(){
	  	  var account = $("#currentSelectAccount").html();
	  	  if(!account){
	  			layer.msg("请先选择一个用户！");
	  			return;
	  	  }
	  	  var param = {"selectAccount":account};
	  	  //隶属仓库
		  var memberOrgId = $("select[name='orgSelect']").val();
		  if(!memberOrgId){
	  			layer.msg("请选择一个隶属仓库！");
	  			return;
		  }
		  	param["memberOrgId"] = memberOrgId; 
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
		  	  url: basePath + "/menuweb/saveAccountMenuWeb", 
		  	  type:"POST",
		  	  dataType: "json",
		  	  data: param,
		  	  success: function(result, textStatus, jqXHR){
	            	layer.closeAll('loading'); //关闭加载中loading效果
		  		  	if(result.status == 200){
		  		  		layer.msg("保存成功！");
		  		  	}else{
		  		  		layer.msg(result.msg);
		  		  	}
		  	  },error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	          }
		  });
  });
});
</script>
</html>