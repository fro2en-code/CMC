<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <title>库存盘点</title>
    <style type="text/css">
    </style>
  </head>
  <body>
	<%------------------------------------------------ 
        					右边内容开始 
        ------------------------------------------------%>
    <div class="layui-form layui-search-form">
		<div class="layui-form-item">
            <!-- <div class="layui-inline"> -->
                <div class="layui-inline">
					<label class="layui-form-label">盘点仓库</label>
					<div class="layui-input-inline">
						<select name="orgSelect" lay-search="">
							<option value="">直接选择或搜索选择</option>
						</select>
					</div>
				</div>
				<div class="layui-inline">
					<label class="layui-form-label">盘点编号</label>
					<div class="layui-input-inline">
						<input type="text" class="layui-input" name="inventoryId">
					</div>
				</div>
                <div class="layui-inline">
					<label class="layui-form-label">开始日期</label>
					<div class="layui-input-inline">
						<input type="text" class="layui-input" id="startDate" name="startDate">
					</div>
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
	                <cmc:button buttonId="33"> <!-- 用户有该按钮权限才会显示以下代码 -->
	                	<button class="layui-btn layui-btn-sm addButton" data-type="">新建盘点</button>
	                </cmc:button>
				</div>
            <!-- </div> -->
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
	</div>
	<%------------------------------------------------ 
        					右边内容结束 
        ------------------------------------------------%>
	</div>
	</div>
</body>
<!-- “新建盘点”弹出框 -->
<script type="text/html" id="createPanel">
    <div class="layui-form layerForm createPanelForm myLabelWidth105" action="">
        <div class="layui-form-item">
            <label class="layui-form-label">盘点仓库<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
					<select name="orgSelect" lay-search="">
						<option value="">直接选择或搜索选择</option>
					</select>
            </div>
        </div>
		<div  class="layui-form-item">
			<label class="layui-form-label">盘点仓库联系人：</label>
			<div class="layui-input-inline">
				<input type="text" name="contactName"  autocomplete="off" class="layui-input">
			</div>
		</div>
		<div  class="layui-form-item">
			<label class="layui-form-label">盘点仓库联系人联系方式：</label>
			<div class="layui-input-inline">
				<input type="text" name="contactPhone"  autocomplete="off" class="layui-input">
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
    var containerCoderOption; 
    var element = layui.element;
    initLeftmenu(element); //初始化左边菜单导航栏
    
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
	  		  	
				//初始化查询条件
            	var options = "<option value=''>直接选择或搜索仓库</option>";
          		$.each( orgList, function(i, obj){
          			options += "<option value='"+obj.orgId+"'>"+obj.orgName+"</option>";
          		});
          		$(".layui-search-form select[name='orgSelect']").html(options);
          		form.render();
	  	  }
	  });
  
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
    
    layer.load(2); //加载中loading效果
    //方法级渲染
    table.render({
        elem: '#LAY_TABLE'
        , loading: true
        ,done : function(res, curr, count) {
        	layer.closeAll('loading'); //关闭加载中loading效果
    	}
        , url: basePath + '/inventory/pagingInventoryMainList' //数据接口
        , cols: [[
    		  {field: 'inventoryOrgName', title: '盘点仓库', width: 170, sort: false}
    		, {field: 'inventoryId', title: '盘点编号', width: 220, sort: false}
            , {field: 'inventoryTime', title: '盘点日期', width: 180, sort: false,templet: function(obj){
	    	  	var result = '';
		  		if(obj.inventoryTime){
		  			result += DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.inventoryTime);
		  		}
	        	return result;
	      	}}
    		, {field: 'createRealName', title: '创建人', width: 120, sort: false}
            , {title: '操作', width:160, sort: false, align:'center',templet: function (d) {
                if (d.inventoryState == 0) {
                	var result ='';
                	<cmc:button buttonId="34"> /* 用户有该按钮权限才会显示以下代码 */
                    return '<a class="layui-btn layui-btn-xs" lay-event="finishInventory">盘点完毕</a>';
                    </cmc:button>
                    return result;
                } else {  
                	var result = '';
                	<cmc:button buttonId="35"> /* 用户有该按钮权限才会显示以下代码 */
                    return '<a class="layui-btn layui-btn-xs" lay-event="expertDifferentInventory">盘点差异Excel下载</a>';
                    </cmc:button>
                    return result;
                }
            }}
            , {field: 'inventoryState', title: '盘点状态', width:100, sort: false
                ,templet: function (d) {
                    if (d.inventoryState == 0) {
                        return "<span value='0'>盘点中</span>";
                    } else {
                        return "<span value='0'>盘点完成</span>";
                    }
                }
            }
            , {field: 'inventoryFinishTime', title: '盘点完毕时间与盘点人', width:250, templet: function(obj){
	    	  	var result = "";
		  		if(obj.finishTime){
		  			result = DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.finishTime);
		  		}
		  		if(obj.finishRealName){
		  			result = result + " " +obj.finishRealName;
		  		}
	        	return result;
	      	}}
            , {field: 'finishOrgName', title: '盘点完毕仓库', width:230, sort: false,templet: function(obj){
	    	  	var result = "";
		  		if(obj.finishOrgName){
		  			result = obj.finishOrgName;
		  		}
	        	return result;
	      	}}
            , {field: 'contactName', title: '盘点仓库联系人与联系方式', width: 260, sort: false,templet: function(obj){
	    	  	var result = "";
		  		if(obj.contactName){
		  			result = '<span class="tableCellPre">'+ result + obj.contactName+'</span>';
		  		}
		  		if(obj.contactPhone){
		  			result = result +  '<span class="tableCellAfter">'+ obj.contactPhone +'</span>';
		  		}
	        	return result;
	      	}}
        ]]
        , id: 'layuiReloadId'
        , page: true
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
                   'inventoryOrgId': $(".layui-search-form select[name='orgSelect']").val(),
                   'inventoryId': $(".layui-search-form input[name='inventoryId']").val(),
                   'startDate': $(".layui-search-form input[name='startDate']").val(),
                   'endDate': $(".layui-search-form input[name='endDate']").val()
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
    	  $(".layui-search-form input[name='startDate']").val("");
    	  $(".layui-search-form input[name='endDate']").val("");
    });
    //新增按钮点击
    layui.$('.addButton').on('click', function () {
    	var layerEditName = layer.open({
            type: 1 //Page层类型
            ,area: ['500px', '400px']
            ,title: '新建盘点'
            ,shade: 0.6 //遮罩透明度
            ,maxmin: true //允许全屏最小化
            ,anim: -1 //0-6的动画形式，-1不开启
            ,content: $("#createPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            ,cancel: function(){
                //alert("关闭啦");
            }
            ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
				var defaultOrg = orgList[0].orgId;
            	var options = "<option value=''>直接选择或搜索仓库</option>";
          		$.each( orgList, function(i, obj){
          			options += "<option value='"+obj.orgId+"'>"+obj.orgName+"</option>";
          		});
          		$(".createPanelForm select[name='orgSelect']").html(options);
                $(".createPanelForm select[name='orgSelect']").val(defaultOrg);
          		form.render();
            }
            ,btn: ['保存']
            ,btn1: function(index){
	                  var param = {};
	                  param['orgId'] = $(".createPanelForm select[name='orgSelect']").val();
                      param['contactName'] = $(".createPanelForm input[name='contactName']").val();
                      param['contactPhone'] = $(".createPanelForm input[name='contactPhone']").val();
	            	  layer.load(2); //加载中loading效果
	                  $.ajax({
	                      url: basePath + "/inventory/addInventoryMain", //新建盘点
                          //url: basePath + "/inventory/addInventoryMainThread", //新建盘点
	                      type: "POST",
	                      dataType: "json",
			      		  data: param,
	                      success: function (result, textStatus, jqXHR) {
	                          layer.closeAll('loading'); //关闭加载中loading效果
	                          layer.msg(result.msg);
	                          if (result.status == 200) {
	                              layer.closeAll();   //成功后，关闭所有弹出框
	                              reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
	                          }
					  	  },error: function(index, upload){
				            	layer.closeAll('loading'); //关闭加载中loading效果
				          }
	                  });
            }
        });
    });

    //监听工具条
    table.on('tool(myLayFilter)', function (obj) {
        var data = obj.data;
        if (obj.event === 'finishInventory') {
        	finishInventory(data);
        } else if (obj.event === 'expertDifferentInventory') {
        	expertDifferentInventory(data); //“盘点差异excel下载”按钮点击
        }
    });
	//“盘点差异excel下载”按钮点击，导出差异报表
    var expertDifferentInventory = function (data) {
    	var param = "?a=1&inventoryId="+data['inventoryId'];
        window.open(basePath + '/inventory/expertDifferentInventory'+param,'target','');
    }

    //"盘点完毕"按钮
    var finishInventory = function (data) {
    	var param = {};
    	param['inventoryId'] = data['inventoryId'];
    	layer.confirm("确认要盘点完毕吗?", { title: "盘点确认" }, function (index){
    	layer.load(2); //加载中loading效果
    	$.ajax({
            url: basePath + "/inventory/finishInventoryDetail",
            type: "POST",
            dataType: "json",
            data: param,
            success: function (result, textStatus, jqXHR) {
            	layer.closeAll('loading'); //关闭加载中loading效果
                layer.msg(result.msg);
                if (result.status == 200) {
                    //layer.closeAll();   //成功后，关闭所有弹出框
                    reloadTableData(); //重新加载table数据
                }
            },error: function(index, upload){
            	layer.closeAll('loading'); //关闭加载中loading效果
         	}
        });
    	});
    }
});
</script>
</html>