<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>用户管理</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="demoTable layui-form layui-search-form myLabelWidth75" >
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">账号名称</label>
				<div class="layui-input-inline">
					<select name="accountSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label" style="width: 45px;">工种</label>
				<div class="layui-input-inline">
					<select name="jobSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">隶属仓库</label>
				<div class="layui-input-inline">
					<select name="orgSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton"
					data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
                <%-- <a href="<%=basePath%>/user/userExcelDownload" class="layui-btn layui-btn-sm" target="_blank">导入模版下载</a> --%>
                <cmc:button buttonId="65"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm" onclick="downloadTemplate('/user/userExcelDownload')">导入模版下载</button>
				</cmc:button>
			</div>
			<div class="layui-inline">
					<cmc:button buttonId="66"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button type="button" class="layui-btn layui-btn-sm " id="uploadExcel">
						<i class="layui-icon"></i>批量导入
						</button>
					</cmc:button>
			</div>
			<div class="layui-inline">
				<cmc:button buttonId="64"> <!-- 用户有该按钮权限才会显示以下代码 -->
				<button class="layui-btn layui-btn-sm addButton" data-type="reload">新增</button>
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
<!-- “新增”弹出框 -->
<script type="text/html" id="ddUserPanel">
<div class="layerForm ddUserPanelForm myLabelWidth105" >
    <form class="layui-form" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">账号名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="account" autocomplete="off" class="layui-input">
            </div>
            <label class="layui-form-label">密码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="password" name="password" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">真实姓名<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="realName" autocomplete="off" class="layui-input">
            </div>
            <label class="layui-form-label">身份证号</label>
            <div class="layui-input-inline">
                <input type="text" name="idCardNum" autocomplete="off"  class="layui-input">
            </div>
        </div>
    </form>
	<div class="my-line-height">
		<span class="my-form-label" style="padding-right: 20px;">隶属仓库<span class="myRedColor">*</span></span>
		<select name="memberOfOrgList" class="multiselect" multiple="multiple">
	    	<option value="">请选择</option>
		</select>
	</div>
	<div class="my-line-height">
		<span class="my-form-label" style="padding-right: 27px;">隶属工种</span>
		<select name="systemJobList" class="multiselect" multiple="multiple">
	    	<option value="">请选择</option>
		</select>
	</div>
    <form class="layui-form" action="">
        <div class="layui-form-item">
            <label class="layui-form-label" style="width: 180px;">是否门型设备账号<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
					<input type="radio" name="isDoor" value="0" title="不是" checked>
    			    <input type="radio" name="isDoor" value="1" title="是">
            </div>
        </div>
	</form>
</div>
</script>
<!-- “编辑用户”弹出框 -->
<script type="text/html" id="editUserPanel">
<div class="layerForm editUserPanelForm myLabelWidth105" >
    <form class="layui-form" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">账号名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="account" autocomplete="off" class="layui-input layui-disabled" disabled="disabled">
            </div>
            <label class="layui-form-label">真实姓名<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="realName" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">身份证号</label>
            <div class="layui-input-inline">
                <input type="text" name="idCardNum" autocomplete="off"  class="layui-input">
            </div>
        </div>
    </form>
	<div class="my-line-height">
		<span class="my-form-label" style="padding-right: 20px;">隶属仓库<span class="myRedColor">*</span></span>
		<select name="memberOfOrgList" class="multiselect" multiple="multiple">
	    	<option value="">请选择</option>
		</select>
	</div>
	<div class="my-line-height">
		<span class="my-form-label" style="padding-right: 27px;">隶属工种</span>
		<select name="systemJobList" class="multiselect" multiple="multiple">
	    	<option value="">请选择</option>
		</select>
	</div>
</div>
</script>
<!-- “修改密码”弹出框 -->
<script type="text/html" id="changePasswordPanel">
    <form class="layui-form layerForm myLabelWidth115 changePasswordPanelForm" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">账号名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="account" autocomplete="off" class="layui-input layui-disabled" disabled="disabled">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">真实姓名<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="realName" autocomplete="off" class="layui-input layui-disabled" disabled="disabled">
            </div>
        </div>
        <div class="layui-form-item" >
            <label class="layui-form-label">新密码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="password" name="newPassword" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item" >
            <label class="layui-form-label">确认新密码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="password" name="confirmPassword" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item" >
            <label class="layui-form-label"></label>
            <div class="layui-input-inline">
                <div style="color:red;">新密码必须为字母、数字、下划线的任意组合，长度为6到15位</div>
            </div>
        </div>
    </form>
</script>

<!-- “打印机设置”弹出框 -->
<script type="text/html" id="printPanel">
	<from class="layui-form layerForm myLabelWidth115 printPanelForm" action="">
		<div class="layui-form-item" style="margin-top: 20px;">
			<label class="layui-form-label">仓库:</label>
			<div class="layui-input-inline">
				<input type="text" name="orgName" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled>
			</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">打印机:</label>
			<div class="layui-input-inline">
				<select name="printName" lay-filter="printPanelForm-printNameFilter" lay-search>
					<option value=""></option>
				</select>
			</div>
		</div>
	</from>
</script>

<script type="text/javascript">

layui.use(['laydate', 'layer', 'table', 'element','form','layedit', 'upload'], function(){
	  var table = layui.table;
	  var layer = layui.layer;
	  var form = layui.form;
	  var layedit = layui.layedit;
      var upload = layui.upload;
      var element = layui.element;
      initLeftmenu(element); //初始化左边菜单导航栏

	  //初始化打印机下拉框
      var printNameList = [];
      $.ajax({
          type: 'POST',
          url: basePath + '/sysPrint/listAllPrintName',
          dataType: 'json',
          success: function (data) {
              printNameList = data.list;
          }
      });

	  //初始化账号名称下拉选择框
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
	  var jobList = []; //当前选中仓库的所有工种列表
	  //初始化工种下拉选择框
	  $.ajax({
	  	  url: basePath + "/job/listCurrentOrgJob",
	  	  type:"POST",
	  	  dataType: "json",
	  	  data: {},
	  	  success: function(result, textStatus, jqXHR){
	  		    var options = "<option value=\"\">直接选择或搜索选择</option>";
	  		    var list = result.list;
	  		  	jobList = list; //赋值给全局变量
		  		$.each( list, function(i, obj){
		  		  	options += "<option value=\""+obj.jobId+"\">"+obj.jobName+"</option>";
		  		});
		  		$("select[name='jobSelect']").html(options);
		  		form.render();
	  	  }
	  });
	  var orgList = []; //当前选中仓库的所有子公司列表，包括自己
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
		  		$("select[name='orgSelect']").html(options);
		  		form.render();
	  	  }
	  });

	  //批量导入
      upload.render({
          elem: '#uploadExcel'
          ,url: basePath+'/user/batchUpload'
          ,accept: 'file' //普通文件
          ,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
          	layer.load(2); //加载中loading效果
          }
          ,done: function(result){
          	layer.closeAll('loading'); //关闭加载中loading效果
              if(result.status == 200){
              		layer.msg('批量导入成功！'); 
              		reloadTableData(); //重新加载table数据
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
        	form.render();
	    }
	    ,url:  basePath + '/user/pagingAllUserByFiliale' //数据接口
	    ,cols: [[
	      {field: 'account', title: '账号名称', width:120, sort: false} 
	      ,{field: 'realName', title: '真实姓名', width:280, sort: false} 
	      ,{field: 'idCardNum', title: '身份证号', width:140, sort: false}
	      ,{field: 'memberOfOrgList', title: '隶属仓库', width:200 ,sort: false,templet: function(obj){
	    	    var result = '';
	    	    if(obj.memberOfOrgList){
	    	    	var length = obj.memberOfOrgList.length;
			  		$.each( obj.memberOfOrgList, function(i, org){
			  			if(obj.memberOfOrgList.length==1){
			  				result = org.orgName;
			  			}else if(obj.memberOfOrgList.length-1 == i){
			  				result += org.orgName;
			  			}else{
			  				result += org.orgName + ", ";
			  			}
			  		});
	    	    }
	        	return result;
	      }} 
	      ,{field: 'systemJobList', title: '工种', width:100, sort: false,templet: function(obj){
	    	    var result = '';
	    	    if(obj.systemJobList){
	    	    	var length = obj.systemJobList.length;
			  		$.each( obj.systemJobList, function(i, job){
			  			if(obj.systemJobList.length==1){
			  				result = job.jobName;
			  			}else if(obj.systemJobList.length-1 == i){
			  				result += job.jobName;
			  			}else{
			  				result += job.jobName + ", ";
			  			}
			  		});
	    	    }
	        	return result;
	      }} 
	      ,{field: 'isActive', title: '用户状态', width:90, sort: false,templet: function(obj){
	  	  		var result = '';
	  	  		var disabledCss = ' class="layui-disabled" ';
	  	  		var disabledStr = ' disabled="disabled" ';
	  	  		
	  	  		<cmc:button buttonId="67">
		  			disabledCss = '';
		  			disabledStr = '';
	  	  		</cmc:button>
	  	  	
		  		if(obj.account == 'admin'){ //超级管理员用户不允许编辑
		  			disabledCss = ' class="layui-disabled" ';
		  			disabledStr = ' disabled="disabled" ';
		  		}
	  			result = '<input type="checkbox" account="'+obj.account+'" value='+obj.isActive+' name="ifKey" lay-skin="switch" lay-filter="isActiveSwitch" lay-text="启用|禁用" '
			         +(obj.isActive == '0' ? 'checked' : '')+'  '+disabledCss + disabledStr+'  >';
		  		return result;
		  }}
	      ,{field: 'isActive', title: '是否门型设备账号', width:150, sort: false,templet: function(obj){
	  	  		var result = '';
	  	  		var disabledCss = ' class="layui-disabled" ';
	  	  		var disabledStr = ' disabled="disabled" ';
	  	  		
	  	  		<cmc:button buttonId="67">
		  			disabledCss = '';
		  			disabledStr = '';
	  	  		</cmc:button>
	  	  	
		  		if(obj.account == 'admin'){ //超级管理员用户不允许编辑
		  			disabledCss = ' class="layui-disabled" ';
		  			disabledStr = ' disabled="disabled" ';
		  		}
	  			result = '<input type="checkbox" account="'+obj.account+'" value='+obj.isActive+' name="ifKey" lay-skin="switch" lay-filter="isDoorSwitch" lay-text="是|否" '
			         +(obj.isDoor == '1' ? 'checked' : '')+'  '+disabledCss + disabledStr+'  >';
		  		return result;
		  }}
	      ,{ title: '操作', width:270, sort: false, align:'center',templet: function(obj){
		  	  		var result = '';
			  		if(obj.account != 'admin'){ //超级管理员用户不允许编辑
			  			var btn1 = '';
			  			var btn2 = '';
			  			var btn3 = '';
                        var btn4 = '';
			  	        <cmc:button buttonId="68"> /* 用户有该按钮权限才会显示以下代码 */
			  	      		btn1 = '<a class="layui-btn layui-btn-xs" lay-event="editUser">编辑用户</a>';
			  	        </cmc:button>
			  	        <cmc:button buttonId="69"> /* 用户有该按钮权限才会显示以下代码 */
			  	      		btn3 = '<a class="layui-btn layui-btn-xs" lay-event="changePassword">修改密码</a>';
			  	        </cmc:button>
                        <cmc:button buttonId="83"> /* 用户有该按钮权限才会显示以下代码 */
                        btn4 = '<a class="layui-btn layui-btn-xs" lay-event="editPrint">打印机设置</a>';
                        </cmc:button>
			  	        result = btn1 + btn2 + btn3 + btn4;
			  		}
			  		return result;
			}}
	      ,{field: 'lastLoginTime', title: '最后登录时间', width:200, sort: false,templet: function(obj){
	    	  	var result = '';
		  		if(obj.lastLoginTime){
		  			result += DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.lastLoginTime);
		  		}
	        	return result;
	      }} 
	      ,{field: 'createTime', title: '创建人与创建时间', width:220, sort: false,templet: function(obj){
	  	  		var result = '';
		  		if(obj.createRealName){
		  			result += '<span class="tableCellPre">'+ obj.createRealName +'</span>';
		  		}
		  		if(obj.createTime){
		  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
		  		}
	        	return result;
	      }}
	      ,{field: 'modifyTime', title: '修改人与修改时间', width:220, sort: false,templet: function(obj){
	    	  		var result = '';
	    	  		if(obj.modifyRealName){
	    	  			result += '<span class="tableCellPre">'+ obj.modifyRealName +'</span>';
	    	  		}
	    	  		if(obj.modifyTime){
	    	  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.modifyTime) +'</span>';
	    	  		}
	    	  		return result;
			}}
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
                	 'selectAccount':$(".demoTable select[name='accountSelect']").val()
      	             ,'jobId':$(".demoTable select[name='jobSelect']").val()
      	             ,'orgId':$(".demoTable select[name='orgSelect']").val()
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
	  
      //监听表格工具条：编辑、修改密码、禁用
      table.on('tool(myLayFilter)', function(obj){
          var data = obj.data;
          if(obj.event === 'editUser'){
              editUser(data);
          } else if(obj.event === 'editUserOrg'){
        	  editUserOrg(data);
          } else if(obj.event === 'changePassword'){
        	  changePassword(data);
          } else if(obj.event === 'editPrint'){
              editPrint(data);}
      });
     //“用户状态”列，启用、禁用按钮点击事件
  	 form.on('switch(isActiveSwitch)', function(data){ 
 		  var param = {};
 		  param['accountInsert'] = $(data.elem).attr('account');
		  if(data.elem.checked){ //如果开启就是启用
			  param['isActiveInsert'] = 0 ;
		  }else{
			  param['isActiveInsert'] = 1 ;
		  }
		  layer.load(2); //加载中loading效果
	      $.ajax({
	             url: basePath + "/user/changeActiveUser",
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
     //“是否门型设备账号”列，是、否按钮切换事件
  	 form.on('switch(isDoorSwitch)', function(data){ 
 		  var param = {};
 		  param['accountInsert'] = $(data.elem).attr('account');
		  if(data.elem.checked){ //是否是门型设备账号。0否，1是
			  param['isDoorInsert'] = 1 ;
		  }else{
			  param['isDoorInsert'] = 0 ;
		  }
		  layer.load(2); //加载中loading效果
	      $.ajax({
	             url: basePath + "/user/changeDoorUser",
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
      //点击表格的“编辑用户”按钮事件
      var editUser = function(data){
          layer.open({
              type: 1 //Page层类型
              ,area: ['800px', '400px']
              ,title: '用户编辑'
              ,shade: 0.6 //遮罩透明度
              ,maxmin: true //允许全屏最小化
              ,anim: -1 //0-6的动画形式，-1不开启
              ,content: $("#editUserPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
              ,cancel: function(){
                  //alert("关闭啦");
              }
              ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
            	  //设置弹出框form表单d值
            	  $(".editUserPanelForm input[name='account']").val(data.account);
            	  $(".editUserPanelForm input[name='realName']").val(data.realName);
            	  $(".editUserPanelForm input[name='idCardNum']").val(data.idCardNum);
                  form.render();

                  //初始化弹出框里下拉框的option选项值——隶属机构
                  var orgOptions = "";
		          $.each(orgList, function(i, obj){
		        	  var selected = '';
			          $.each(data.memberOfOrgList, function(i, org){
			        	  if(obj.orgId == org.orgId){
			        		  selected = 'selected'; //勾选隶属机构
			        	  }
					  });
			          orgOptions += "<option value='"+obj.orgId+"' "+selected+">"+obj.orgName+"</option>";
				  });
		          $(".editUserPanelForm select[name='memberOfOrgList']").html(orgOptions);
          	   	  $(".editUserPanelForm select[name='memberOfOrgList']").multiselect({ 
	          	        enableFiltering: true,
	          	        buttonWidth: '490px'
          	      }).multiselect('rebuild');
          	   	  
                  //初始化弹出框里下拉框的option选项值——隶属工种
                  var jobOptions = "";
		          $.each(jobList, function(i, obj){
		        	  var selected = '';
			          $.each(data.systemJobList, function(i, job){
			        	  if(obj.jobId == job.jobId){
			        		  selected = 'selected'; //勾选隶属工种
			        	  }
					  });
			          jobOptions += "<option value='"+obj.jobId+"' "+selected+">"+obj.jobName+"</option>";
				  });
		          $(".editUserPanelForm select[name='systemJobList']").html(jobOptions);
          	   	  $(".editUserPanelForm select[name='systemJobList']").multiselect({ 
	          	        enableFiltering: true,
	          	        buttonWidth: '490px'
          	      });
              }
              ,btn: ['保存']
              ,btn1: function(index){
	                  var param = {};
	                  param['accountEdit'] = $(".editUserPanelForm input[name='account']").val();
	                  param['realNameEdit'] = $(".editUserPanelForm input[name='realName']").val();
	                  param['idCardNumEdit'] = $(".editUserPanelForm input[name='idCardNum']").val();
	                  param['orgList'] = $(".editUserPanelForm select[name='memberOfOrgList']").val();
	                  param['jobList'] = $(".editUserPanelForm select[name='systemJobList']").val();
	            	  layer.load(2); //加载中loading效果
	                  $.ajax({
			      		  url: basePath + "/user/editSystemUser",
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

      //点击表格的“修改密码”按钮事件
      var changePassword = function(data){
          layerEditName = layer.open({
              type: 1 //Page层类型
              ,area: ['500px', '500px']
              ,title: '修改密码'
              ,shade: 0.6 //遮罩透明度
              ,maxmin: true //允许全屏最小化
              ,anim: -1 //0-6的动画形式，-1不开启
              ,content: $("#changePasswordPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
              ,cancel: function(){
                  //alert("关闭啦");
              }
              ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
            	  //设置弹出框form表单d值
            	  $(".changePasswordPanelForm input[name='account']").val(data.account);
            	  $(".changePasswordPanelForm input[name='realName']").val(data.realName);
                  form.render();
              }
              ,btn: ['保存']
              ,btn1: function(index){
	                  var param = {};
	                  param['selectAccount'] = $(".changePasswordPanelForm input[name='account']").val();
	                  param['newPassword'] = $(".changePasswordPanelForm input[name='newPassword']").val();
	                  param['confirmPassword'] = $(".changePasswordPanelForm input[name='confirmPassword']").val();
	                  layer.load(2); //加载中loading效果
	                  $.ajax({
			      		  url: basePath + "/user/changePassword",
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
      //重置按钮
      $('.resetButton').on('click', function(){
      	  $(".layui-search-form select[name='accountSelect']").val("");
      	  $(".layui-search-form select[name='jobSelect']").val(""); 
      	  $(".layui-search-form select[name='orgSelect']").val("");
      	  form.render();
      });
	  //点击"新增"按钮
	  layui.$('.demoTable .addButton').on('click', function(){
          layerEditName = layer.open({
              type: 1 //Page层类型
              ,area: ['800px', '450px']
              ,title: '用户新增'
              ,shade: 0.6 //遮罩透明度
              ,maxmin: true //允许全屏最小化
              ,anim: -1 //0-6的动画形式，-1不开启
              ,content: $("#ddUserPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
              ,cancel: function(){
                  //alert("关闭啦");
              }
              ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
	        	  	  $(".ddUserPanelForm input[name='account']").val('');
	        	  	  $(".ddUserPanelForm input[name='password']").val('');
	                  form.render();
	
	                  //初始化弹出框里下拉框的option选项值——隶属机构
	                  var orgOptions = '';
			          $.each(orgList, function(i, obj){
				          orgOptions += "<option value='"+obj.orgId+"' >"+obj.orgName+"</option>";
					  });
			          $(".ddUserPanelForm select[name='memberOfOrgList']").html(orgOptions);
	          	   	  $(".ddUserPanelForm select[name='memberOfOrgList']").multiselect({ 
		          	        enableFiltering: true,
		          	        buttonWidth: '490px'
	          	      }).multiselect('rebuild');
	
	                  //初始化弹出框里下拉框的option选项值——隶属工种
	                  var jobOptions = '';
			          $.each(jobList, function(i, obj){
				          jobOptions += "<option value='"+obj.jobId+"'>"+obj.jobName+"</option>";
					  });
			          $(".ddUserPanelForm select[name='systemJobList']").html(jobOptions);
	          	   	  $(".ddUserPanelForm select[name='systemJobList']").multiselect({ 
		          	        enableFiltering: true,
		          	        buttonWidth: '490px'
	          	      }).multiselect('rebuild');
              }
              ,btn: ['保存']
              ,btn1: function(index){
	                  var param = {};
	                  param['accountInsert'] = $(".ddUserPanelForm input[name='account']").val();
	                  param['passwordInsert'] = $(".ddUserPanelForm input[name='password']").val();
	                  param['realNameInsert'] = $(".ddUserPanelForm input[name='realName']").val();
	                  param['idCardNumInsert'] = $(".ddUserPanelForm input[name='idCardNum']").val();
	                  param['orgList'] = $(".ddUserPanelForm select[name='memberOfOrgList']").val();
	                  param['jobList'] = $(".ddUserPanelForm select[name='systemJobList']").val();
	                  $(".ddUserPanelForm input:radio[name='isDoor']").each(function () { 
	             		 	if(this.checked){
	                 			param['isDoorInsert'] = this.value; //获取单选框的值：是否门型设备账号
	             		 	}
	        	        })
	            	  layer.load(2); //加载中loading效果
	                  $.ajax({
			      		  url: basePath + "/user/addSystemUser",
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
	  });

	  //点击打印机设置按钮
	var editPrint = function (data) {
        layerEditName = layer.open({
            type: 1 //Page层类型
            ,area: ['500px', '300px']
            ,title: '打印机设置'
            ,shade: 0.6 //遮罩透明度
            ,maxmin: true //允许全屏最小化
            ,anim: -1 //0-6的动画形式，-1不开启
            ,content: $("#printPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            ,cancel: function(){
                //alert("关闭啦");
            }
            ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
            	//设置仓库的值
                $(".printPanelForm input[name='orgName']").val("<%=sessionUser.getCurrentSystemOrg().getOrgName() %>");

                var printNameOption = "<option value=''>直接选择或搜索选择</option>";
                for (var i=0;i<printNameList.length;i++){
                    printNameOption += "<option value='" + printNameList[i].printCode + "'>" + printNameList[i].printName+ "</option>";
                }
                $(".printPanelForm select[name='printName']").html(printNameOption);
                form.render();
                
                layer.load(2); //加载中loading效果
                $.ajax({
                    url: basePath + "/sysPrint/queryPrintNameByOrgAndAccount",
                    type:"POST",
                    dataType: "json",
                    data: {"printAccount":data.account},
                    success: function(result, textStatus, jqXHR){
                        layer.closeAll('loading'); //关闭加载中loading效果
                        if(result.status == 200 && result.bean){
                            $(".printPanelForm select[name='printName']").val(result.bean.printCode);
                            form.render();
                        }
                    },error: function(index, upload){
                        layer.closeAll('loading'); //关闭加载中loading效果
                    }
                });
            }
            ,btn: ['保存']
            ,btn1: function(index){
                var param = {};
                param['printName'] = $(".printPanelForm select[name='printName']").text();
                param['printCode'] = $(".printPanelForm select[name='printName']").val();
                param['printAccount'] = data.account;
                layer.load(2); //加载中loading效果
                $.ajax({
                    url: basePath + "/sysPrint/addPrint",
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
});
</script>
</html>