<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>组托和解托</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
	<div class="demoTable layui-form layui-search-form myLabelWidth85">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">EPC编号</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" name="epcId">
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">组托状态</label>
				<div class="layui-input-inline">
					<select name="groupState" lay-search="">
						<option value="">请选择</option>
						<option value="0">已组托</option>
						<option value="1">已解托</option>
					</select>
				</div>
				<div class="layui-input-inline">
					<button class="layui-btn layui-btn-sm queryButton"
						data-type="reload">查询</button>
					<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				</div>
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
					<cmc:button buttonId="7"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button class="layui-btn layui-btn-sm relieveGroupButton">解托</button>
					</cmc:button>
					<cmc:button buttonId="77"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button class="layui-btn layui-btn-sm addNewGroupButton">组托</button>
					</cmc:button>
					<cmc:button buttonId="9"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button class="layui-btn layui-btn-sm exportButton">导出当前查询结果</button>
					</cmc:button>
			</div>
		</div>

		<table class="layui-table">
			<colgroup>
				<col width="50">
				<col width="200">
				<col width="200">
				<col width="200">
				<col width="200">
				<col width="200">
				<col width="130">
				<col width="250">
				<col width="250">
				<col width="250">
				<col width="250">
			</colgroup>
			<thead>
				<tr>
					<th><input type="checkbox" name="checkBoxAll" lay-filter="checkBoxLayFilterAll" lay-skin="primary" ></th>
					<th>组托EPC编号</th>
					<th>EPC编号</th>
					<th>器具代码</th>
					<th>器具类型</th>
					<th>器具名称</th>
					<th>组托状态</th>
					<th>创建人与创建时间</th>
					<th>修改人和修改时间</th>
					<th>组托识别号</th>
					<th>最后所在仓库</th>
				</tr>
			</thead>
			<tbody id="myTableBody"></tbody>
			<tfoot>
				<tr>
					<td colspan="11" id="myTablePaging"
						style="padding-top: 0px; padding-bottom: 0px;"></td>
				</tr>
			</tfoot>
		</table>


		<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>

<!-- "组托"弹出框 -->
<script type="text/html" id="newGroupPanel">
	<div class="layui-form newGroupPanelForm layerForm myLabelWidth130" action="">
		<div id="epcIdInsertDiv" class="layui-form-item">
			<label class="layui-form-label">组托EPC编号<span class="myRedColor">*</span>:</label>
            <div class="layui-input-inline">
				<textarea name="epcIdForGroup" placeholder="请输入需要组托的epc编号，多个epc编号之间用;分号隔开" class="layui-textarea" style="width:500px;height:200px;"></textarea>
            </div>
		</div>
	</div>
</script>

<script type="text/javascript">
layui.use(['laydate','form', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element'], function(){
	  var laydate = layui.laydate //日期
	  ,laypage = layui.laypage //分页
	  layer = layui.layer //弹层
	  ,table = layui.table //表格
	  ,element = layui.element //元素操作
	  ,form = layui.form; //表单
      var element = layui.element;
      initLeftmenu(element); //初始化左边菜单导航栏
	  
	  var limitInit = 50;
	  var curr = 1;
	  var queryPagingRenderCount = 2;	 //查询后分页条渲染次数
	  
	  var loadTableData = function(limit, curr){
		  var param = {"currentPage":curr, "pageSize":limit};
		  param["epcId"] = $(".layui-search-form input[name='epcId']").val();
		  param["groupState"] = $(".layui-search-form select[name='groupState']").val();
		  layer.load(2); //加载中loading效果
		  $.ajax({
		  	  url: basePath + "/containerGroup/pagingAllGroup", 
		  	  type:"POST",
		  	  dataType: "json",
		  	  data: param,
		  	  success: function(result, textStatus, jqXHR){
		  			layer.closeAll('loading'); //关闭加载中loading效果
		  			$("#myTableBody").html("");
		  		  	if(result.status == 200){
		  		  		var groupList = result.data;
		  		  		
		  		  		//如果没有数据
		  		  		if(groupList.length == 0){
			  		  		var html = "<tr> <td colspan='11' style='text-align: center;'>无数据</td> </tr>";
			  		  		$("#myTableBody").html(html);
				  		  	table.render();
				  		  	form.render();
				  		  	return;
		  		  		}
		  		  		
		  		  		groupList.push({});   //故意新增一条数据，以免each循环时不处理最后一条
		  		  		var tableHtml = "";
		  		  		var rowspanGroup; //需要合并单元格的组托对象
		  		  		var lastGroupId = "";
		  		  		var tr2 = ""; 
		  		  		var rowspan = 1;
			  		  	$.each( groupList, function(i, group){
			  		  		if(i==0 || group.groupId != lastGroupId){ //判断组托EPC编号是否一致
			  		  			if(!rowspanGroup){ 
			  		  				rowspanGroup = group;
			  		  			}else{
				  		  		    //拼接所有已知html  
					  		  		var tr1 = "<tr>";

					  		  		tr1 += "<td>";
					  		  		if(rowspanGroup.groupState == 0){
					  		  			tr1 += "<input type='checkbox' name='groupCheckBox' lay-filter='groupCheckBoxFilter' groupState='"+rowspanGroup.groupState+"' groupId='"+rowspanGroup.groupId+"' value='"+rowspanGroup.epcId+"' lay-skin='primary' >";
					  		  		}
					  		  		tr1 += "</td>";
					  		  		tr1 += "<td rowspan='"+rowspan+"' style='text-align:center;'>"+rowspanGroup.groupEpcId+"<br/>("+rowspanGroup.groupType+")器具总数："+rowspanGroup.groupNumber+"</td>"; //组托EPC编号
					  		  		tr1 +=  buildTdHtml(rowspanGroup);

				  		  		    tableHtml += tr1;  //先拼接第一个合并单元格的行
				  		  		    tableHtml += tr2;   //再拼接后面合并单元格的行
				  		  			tr2 = "";   //清空
			  		  				rowspanGroup = group;
			  		  			}
			  		  		    rowspan = 1;
			  		  		}else{
				  		  		tr2 += "<tr>";
				  		  		tr2 += "<td>";
				  		  		if(rowspanGroup.groupState == 0){
				  		  			tr2 += "<input type='checkbox' name='groupCheckBox' lay-filter='groupCheckBoxFilter' groupState='"+rowspanGroup.groupState+"' groupId='"+group.groupId+"' value='"+group.epcId+"' lay-skin='primary' >";
				  		  		}
				  		  		tr2 += "</td>";
				  		  		//tr2 += "<td>"+group.groupEpcId+"</td>"; //组托EPC编号
				  		  		tr2 +=  buildTdHtml(group);
			  		  		    rowspan += 1;
			  		  		}
		  		  			lastGroupId = rowspanGroup.groupId;
			  		  	});
			  		  	
			  		    $("#myTableBody").html(tableHtml);
		  		  	}else{
		  		  		var tableHtml = "";
		  		  		tableHtml += "<tr><td colspan='9' style='text-align:center;'>"+result.msg+"</td></tr>"; 
			  		    $("#myTableBody").html(tableHtml);
		  		  	}
		  		  	table.render();
		  		  	form.render();
		  		  	
 					//重新渲染分页条
 					queryPagingRenderCount = 1;
		  		    renderLaypage(result);
		  		    
		  		    //"全选"复选框监听事件
		  		    checkBoxAllMonitor();
			  	},error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	         	}
		  });
	  }

	  var checkBoxAllMonitor = function(){
		  //监听复选框点击事件
		  form.on('checkbox(checkBoxLayFilter)', function(data){
			   if(!data.elem.checked){  //如果未选中
				   $("input:checkbox[name='checkBoxAll']").each(function () { 
			            this.checked = false; 
			       }) 
			   }
			   form.render();
		  }); 

		  //监听"全选"复选框点击事件
		  form.on('checkbox(checkBoxLayFilterAll)', function(data){
			   if(data.elem.checked){ //如果选中
				   $("input:checkbox[name='groupCheckBox']").each(function () { 
			            var groupState = $(this).attr("groupState");
			            if(groupState == 0){
			            	this.checked = true;
			            }else{
			            	this.checked = false;
			            }
			       }) 
			   }else{ //如果未选中
				   $("input:checkbox[name='groupCheckBox']").each(function () { 
			            this.checked = false; 
			       }) 
			   }
			   form.render();
		  }); 

		  //如果反选任何一个数据行复选框，则反选"全选"复选框
		  form.on('checkbox(groupCheckBoxFilter)', function(data){
			   if(!data.elem.checked){ //如果选中
				   $("input:checkbox[name='checkBoxAll']").each(function () { 
			            this.checked = false; 
			       }) 
			   }
			   form.render();
		  }); 
	  }
	  
	  var buildTdHtml = function(group){
		    //var tr2 = "<td>"+group.epcId+"("+group.groupId+")</td>"; //EPC编号
		    var trayLineStyle = "";
		    if(group.epcId == group.groupEpcId){
		    	trayLineStyle = " style='color:red;' ";
		    }
		    
		    var tr2 = "<td "+trayLineStyle+">"+group.epcId+"</td>"; //EPC编号
	  		tr2 += "<td "+trayLineStyle+">"+group.containerCode+"</td>"; //器具代码
	  		tr2 += "<td "+trayLineStyle+">"+group.containerTypeName+"</td>"; //器具类型
	  		tr2 += "<td "+trayLineStyle+">"+group.containerName+"</td>"; //器具名称
	  		var groupFlag = "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#xe618;</i>";
	  		tr2 += "<td>"+(group.groupState==0?groupFlag:"已解托")+"</td>"; //组托状态0已组托 1已解托
	  		
		  	var createTimeCell = '';
	  		if(group.createRealName){
	  			createTimeCell += '<span class="tableCellPre">'+ group.createRealName +'</span>';
	  		}
	  		if(group.createTime){
	  			createTimeCell += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(group.createTime) +'</span>';
	  		}
	  		tr2 += "<td>"+createTimeCell+"</<td>"; //创建人和创建时间

		  	var modifyTimeCell = '';
	  		if(group.modifyRealName){
	  			modifyTimeCell += '<span class="tableCellPre">'+ group.modifyRealName +'</span>';
	  		}
	  		if(group.modifyTime){
	  			modifyTimeCell += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(group.modifyTime) +'</span>';
	  		}
	  		tr2 += "<td>"+modifyTimeCell+"</<td>"; //修改人和修改时间
	  		tr2 += "<td>"+group.groupId+"</<td>"; //组托识别号
	  		tr2 += "<td>"+group.orgName+"</<td>"; //最后所在仓库
	  		
		  	tr2 += "</<tr>";
		  	return tr2;
	  }
	  
	  var renderLaypage = function(result){
		  //渲染分页条
		  laypage.render({
			    elem: 'myTablePaging'
			    ,count: result.total
			    ,first: '首页',last: '尾页',prev:'上一页',next:'下一页'
			    ,layout: ['count','limit', 'prev', 'page', 'next',  'skip']
			    ,curr: result.currentPage //设定初始在第 1 页
		        ,limit: result.pageSize  //每页多少条
		        ,limits: [50, 100, 200] //支持每页数据条数选择
		        ,groups: 10 //显示 10 个连续页码
			    ,jump: function(obj){
			    	if(queryPagingRenderCount != 1){
				    	limitInit = obj.limit;  //每页多少条，每次都更新为最新
				  	  	loadTableData(obj.limit, obj.curr);
			    	}
 					queryPagingRenderCount += 1;
			    }
		  });
	  }
	  var initResult = {"pageSize":limitInit, "total":0, "currentPage":1};
	  renderLaypage(initResult);
	  
	  //点击查询按钮
	  layui.$('.demoTable .queryButton').on('click', function(){
		  curr = 1;
		  loadTableData(limitInit, curr);
	  });
	  //重置按钮
      layui.$('.resetButton').on('click', function(){
    	  $(".layui-search-form input[name='epcId']").val("");
      	  $(".layui-search-form select[name='groupState']").val("");
      		form.render();
      });
	  //点击“解托”按钮
	  layui.$('.demoTable .relieveGroupButton').on('click', function(){
	  	  //菜单选中值
	  	  var epcIdList = [];
		  layui.$("input:checkbox[name='groupCheckBox']").each(function () { 
					var groupCheckBox = this;
					if(groupCheckBox.checked){
						epcIdList.push(groupCheckBox.value);
					}  
	      }) 
		  var param = {};
		  param["epcIdList"] = epcIdList;
		  layer.load(2); //加载中loading效果
		  $.ajax({
		  	  url: basePath + "/containerGroup/relieveGroup", 
		  	  type:"POST",
		  	  dataType: "json",
		  	  data: param,
		  	  success: function(result, textStatus, jqXHR){
		  			layer.closeAll('loading'); //关闭加载中loading效果
		  			if(result.status == 200){
		  				layer.msg('解托成功！');
		  				loadTableData(limitInit, curr);  //刷新列表
		  			}else{
		  				layer.msg(result.msg);
		  			}
			  	},error: function(index, upload){
	            	layer.closeAll('loading'); //关闭加载中loading效果
	         	}
		  });
	  });
	  //点击“组托”按钮
	  layui.$('.demoTable .addNewGroupButton').on('click', function(){
			  layerEditName = layer.open({
	              type: 1 //Page层类型
	              , area: ['800px', '500px']
	              , title: '组托'
	              , shade: 0.6 //遮罩透明度
	              , maxmin: true //允许全屏最小化
	              , anim: -1 //0-6的动画形式，-1不开启
	              , content: $("#newGroupPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
	              , cancel: function () {
	                  //alert("关闭啦");
	          } 
	          , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
	              form.render();
	          }
	          , btn: ['保存']
	          , btn1: function (index) {
		    	  	  var epcIdList = [];
		    		  var epcIdForGroupVal = layui.$("textarea[name='epcIdForGroup']").val();
		    		  if(!epcIdForGroupVal){
		    			  layer.msg("组托器具不能为空！");
		    			  return;
		    		  }
		    		  var param = {};
		    		  param["epcId"] = epcIdForGroupVal.split(";");
		    		  layer.load(2); //加载中loading效果
		    		  $.ajax({
		    		  	  url: basePath + "/appContainer/createContainerGroup", 
		    		  	  type:"POST",
		    		  	  dataType: "json",
		    		  	  data: param,
		    		  	  success: function(result, textStatus, jqXHR){
		    		  			layer.closeAll('loading'); //关闭加载中loading效果
		    		  			if(result.status == 200){
	    							layer.closeAll('page');   //成功后，关闭所有弹出框
		    		  				layer.msg('组托成功！');
		    		  				loadTableData(limitInit, curr);  //刷新列表
		    		  			}else{
		    		  				layer.msg(result.msg);
		    		  			}
		    			  	},error: function(index, upload){
		    	            	layer.closeAll('loading'); //关闭加载中loading效果
		    	         	}
		    		  });
	          }
	      	});
	  });

    layui.$('.exportButton').on('click', function () {
        var param ="?a=1";
        var startDate = $(".layui-search-form input[name='startDate']").val();
        var endDate = $(".layui-search-form input[name='endDate']").val();
        var groupState = $(".layui-search-form select[name='groupState']").val();
        if (groupState){
            param+="&groupState="+groupState;
        }
        if (startDate) {
            param += "&startDate=" + startDate;
        }
        if (endDate){
            param+="&endDate="+endDate;
        }
        window.open(basePath + '/containerGroup/expertToExcel'+param,'targer','');

    });
});
</script>
</html>