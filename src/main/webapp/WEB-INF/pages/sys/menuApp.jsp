<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>用户app端权限管理</title>
<style type="text/css">
#currentSelectAccount {
	display: -moz-inline-box;
	display: inline-block;
	width: 150px;
}
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="demoTable layui-form layui-search-form myLabelWidth60">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">登录名</label>
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
				<button class="layui-btn layui-btn-sm queryButton"
					data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
			</div>
		</div>
	</div>

	<div>
		<strong>选中账号：</strong><span id="currentSelectAccount"></span> <strong>工种：</strong><span
			id="accountJobs"></span>
	</div>

	<table class="layui-hide" id="LAY_TABLE" lay-filter="myTableLayFilter"></table>

	<div class="layui-form demoTable"
		style="text-align: center; margin: 20px 0px 30px 0px;">
		<div class="layui-form-item">
			<cmc:button buttonId="71"> <!-- 用户有该按钮权限才会显示以下代码 -->
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
layui.use(['laydate', 'layer', 'table', 'element','form','layedit'], function(){
	    var table = layui.table;
	    var layer = layui.layer;
	    var form = layui.form;
	    var element = layui.element;
	    initLeftmenu(element); //初始化左边菜单导航栏
	    
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

	  //初始化登录名下拉选择框
	  $.ajax({
	  	  url: basePath + "/user/listAllUserByFiliale",
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {},
	  	  success: function(result, textStatus, jqXHR){
	  		    var options = "<option value=\"\">直接选择或搜索选择</option>";
	  		    var list = result.list;
		  		$.each( list, function(i, obj){
		  		  	options += "<option value=\""+obj.account+"\">"+obj.account+" ("+obj.realName+")</option>";
		  		});
		  		$("select[name='accountSelect']").html(options);
		  		form.render();
	  	  }
	  });
		//重置按钮
	    $('.resetButton').on('click', function(){
	    	  $(".layui-search-form select[name='accountSelect']").val("");
	    	  $("select[name='orgSelect']").val("");
	    	  form.render();
	    });
	
	  layer.load(2); //加载中loading效果
	  //方法级渲染
	  var myLayTable = table.render({
	    elem: '#LAY_TABLE'
	    ,loading:true 
        ,done : function(res, curr, count) {
        	layer.closeAll('loading'); //关闭加载中loading效果
    	}
	    ,url:  basePath + '/menuapp/pagingAllMenu' //数据接口
	    ,cols: [[
		      {field: 'menuId', width:40
		    	  ,title: '<input type="checkbox" name="menuCheckBoxAll" lay-filter="menuCheckBoxLayFilterAll" lay-skin="primary" >'
		    	  ,templet: function(data){
		          return '<input type="checkbox" name="menuCheckBox" lay-filter="menuCheckBoxLayFilter" lay-skin="primary" value="'+data.menuId+'">'
		      }}
		      ,{field: 'menuName', title: '菜单名称', width:200, sort: false} //, fixed: 'left'
		      ,{field: 'menuRemark', title: '菜单描述', sort: false}
	    ]]
	    ,id: 'layuiTableId'
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
		    } 
	  });

	  //监听复选框点击事件
	  form.on('checkbox(menuCheckBoxLayFilter)', function(data){
		   if(!data.elem.checked){  //如果未选中
			   $("input:checkbox[name='menuCheckBoxAll']").each(function () { 
		            this.checked = false; 
		       }) 
		   }
		   form.render();
	  }); 

	  //监听"全选"复选框点击事件
	  form.on('checkbox(menuCheckBoxLayFilterAll)', function(data){
		   if(data.elem.checked){ //如果选中
			   $("input:checkbox[name='menuCheckBox']").each(function () { 
		            this.checked = true; 
		       }) 
		   }else{ //如果未选中
			   $("input:checkbox[name='menuCheckBox']").each(function () { 
		            this.checked = false; 
		       }) 
		   }
		   form.render();
	  }); 
	  
	  //点击查询按钮
	  layui.$('.queryButton').on('click', function(){
		  	  var account = $("select[name='accountSelect']").val();
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
			  	  url: basePath + "/user/listAccountMenuApp", 
			  	  type:"POST",
			  	  dataType: "json",
			  	  data: {'selectAccount':account,"selectMemberOrgId":selectMemberOrgId},
			  	  success: function(result, textStatus, jqXHR){
		  				layer.closeAll('loading'); //关闭加载中loading效果
		  				if(result.status != 200){
		  					layer.msg(result.msg);
		  					return;
		  				}
			  			var systemUser = result.bean;
			  			var account = systemUser.account;
			  			$("#currentSelectAccount").html(account);
			  			//清空全选
			  			$("input:checkbox[name='menuCheckBoxAll']").each(function () {  //清空复选框
				            this.checked = false; 
				       })
					    form.render();
			  			//清空已勾选菜单
		  				layui.$("input:checkbox[name='menuCheckBox']").each(function () { 
		  					var jobCheckBox = this;
		  					jobCheckBox.checked = false;  
				         }) 
						//把该账号拥有的菜单勾选上
			  			var	systemMenuAppList = systemUser.systemMenuAppList;
			  			$.each( systemMenuAppList, function(i, obj){
			  				$.each( layui.$("input:checkbox[name='menuCheckBox']"), function(i, menu){
			  					var jobCheckBox = this;
			  					if(obj.menuId == jobCheckBox.value){
			  						jobCheckBox.checked = true;  
				  				}
					  		});
				  		});
			  			//把该账号拥有的工种列出来
			  			var	systemJobList = systemUser.systemJobList;
			  			var jobHtml = "";
			  			$.each( systemJobList, function(i, obj){
			  				jobHtml += obj.jobName + "、";
				  		});
			  			$("#accountJobs").html(jobHtml);
			  			
		  				form.render(); //因为对复选框进行选中，所以要重新渲染表单
			  	  },error: function(index, upload){
		            	layer.closeAll('loading'); //关闭加载中loading效果
		          }
			  });
	  });
	  
	  //点击“保存新权限”按钮
	  layui.$('.demoTable .saveButton').on('click', function(){
		  	  var account = $("select[name='accountSelect']").val();
		  	  if(!account){
		  		  layer.msg("必须选择一个账号");
		  		  return;
		  	  } 
		  	  var selectMemberOrgId = $("select[name='orgSelect']").val();
		  	  if(!selectMemberOrgId){
		  		  layer.msg("必须选择一个隶属仓库");
		  		  return;
		  	  }
		  	  var param = {"selectAccount":account};
		  	  //菜单选中值
		  	  var menuList = [];
			  layui.$("input:checkbox[name='menuCheckBox']").each(function () { 
  					var jobCheckBox = this;
  					if(jobCheckBox.checked){
  						menuList.push(jobCheckBox.value);
  					}  
		      }) 
		  	  param["menuList"] = menuList;
			  param["selectMemberOrgId"] = selectMemberOrgId;
	     	  layer.load(2); //加载中loading效果
		  	  //保存账号新的app菜单权限
			  $.ajax({
			  	  url: basePath + "/menuapp/saveAccountMenuApp", 
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