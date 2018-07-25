<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>维修级别管理</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="demoTable layui-search-form myLabelWidth105" id="maintainPanel">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">维修级别名称</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" name="maintainLevelName">
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="51"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm addButton" data-type="">新增</button>
				</cmc:button>
			</div>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

		<script type="text/html" id="bar">
			<cmc:button buttonId="52">
        		<a class="layui-btn layui-btn-xs" lay-event="editMaintain">编辑</a>
			</cmc:button>
    	</script>



		<span style="width: 100px"></span>
		<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
	</div>
	</div>
</body>
<!-- 新增维修级别模版 -->
<script type="text/html" id="addMaintainPanel">
    <form class="layui-form layerForm addMaintainPanelForm myLabelWidth175">
		<div class="layui-form-item">
            <label class="layui-form-label">维修级别<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="maintainLevel" placeholder="例如：A" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div id="areaNameInput" class="layui-form-item">
            <label class="layui-form-label">维修级别名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="maintainLevelName" lay-verify="required" placeholder="例如：轻度维修" autocomplete="off" class="layui-input";>
            </div>
        </div>
		<div class="layui-form-item">
            <label class="layui-form-label">维修时间限制(小时)<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="maintainHour" placeholder="例如：1" autocomplete="off" class="layui-input">
            </div>
        </div>

    </form>
</script>
<!-- 编辑维修级别模版 -->
<script type="text/html" id="editMaintainPanel">

    <form class="layui-form layerForm editMaintainPanelForm myLabelWidth175">
		<div class="layui-form-item">
            <label class="layui-form-label">维修级别<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="maintainLevel" placeholder="例如：A" autocomplete="off" class="layui-input layui-disabled" disabled="disabled">
            </div>
        </div>
        <div id="areaNameInput" class="layui-form-item">
            <label class="layui-form-label">维修级别名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="maintainLevelName" lay-verify="required" placeholder="例如：轻度维修" autocomplete="off" class="layui-input";>
            </div>
        </div>
		<div class="layui-form-item">
            <label class="layui-form-label">维修时间限制(小时)<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="maintainHour" placeholder="例如：1" autocomplete="off" class="layui-input">
            </div>
        </div>

    </form>
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
        
        layer.load(2); //加载中loading效果
        //方法级渲染
        table.render({
            elem: '#LAY_TABLE'
            ,loading:true
            ,url:  basePath + '/mainTainLevel/pagingMainTainLevel' //数据接口
            ,done : function(res, curr, count) {
            	layer.closeAll('loading'); //关闭加载中loading效果
        		//分类显示中文名称(先保留)
        		 $("[data-field='isDefault']").children().each(function() {
        			if ($(this).text() == '1') {
        				$(this).text("非默认入库区域")
        			} else if ($(this).text() == '0') {
        				$(this).text("默认入库区域")
        			}
        		})
        	}
            ,cols: [[
               {field: 'maintainLevel', title: '维修级别', width:160, sort: false} //, fixed: 'left'
              ,{field: 'maintainLevelName', title: '维修级别名称', width:190, sort: false}
              ,{field: 'maintainHour', title: '维修时间限制(小时)', width:160, sort: false}
              ,{field: 'createOrgName', title: '创建公司', width:180, sort: false}
              ,{ title: '操作', width:90, sort: false, align:'center', toolbar: '#bar'}
              ,{field: 'createRealName', title: '创建人和创建时间', width:250, sort: false,templet: function(obj){
          	    var result = '';
	  		  		if(obj.createRealName){
	  		  			result += '<span class="tableCellPre">'+ obj.createRealName +'</span>';
	  		  		}
	  		  		if(obj.createTime){
	  		  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
	  		  		}
	  	        	return result;
            }}
              ,{field: 'modifyRealName', title: '修改人和修改时间', width:250, sort: false,templet: function(obj){
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
                	   'maintainLevelName':$(".layui-search-form input[name='maintainLevelName']").val()
                   }
            };
          	if(currValue){
          		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
          	}
        	table.reload('layuiReloadId', reloadParam);
        }
        layui.$('.demoTable .queryButton').on('click', function(){
        	reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
        });
        //重置按钮
        layui.$('.demoTable .resetButton').on('click', function(){
        	  $("#maintainPanel input[name='maintainLevelName']").val("");
      
        });
      //新增按钮点击
        layui.$('.demoTable  .addButton').on('click', function(){
            layerEditName = layer.open({
                type: 1 //Page层类型
                ,area: ['500px', '300px']
                ,title: '新增维修级别'
                ,shade: 0.6 //遮罩透明度
                ,maxmin: true //允许全屏最小化
                ,anim: -1 //0-6的动画形式，-1不开启
                ,content: $("#addMaintainPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                ,cancel: function(){
                    //alert("关闭啦");
                }
                ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    form.render();
                }
                ,btn: ['保存']
                ,btn1: function(index){
                	var param = {};
                	param["maintainLevel"] = $(".addMaintainPanelForm input[name='maintainLevel']").val();
                	param["maintainLevelName"] = $(".addMaintainPanelForm input[name='maintainLevelName']").val();
                	param["maintainHourStr"] = $(".addMaintainPanelForm input[name='maintainHour']").val();
                	layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/mainTainLevel/addMainTainLevel",
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

        //监听工具条
        table.on('tool(myLayFilter)', function(obj){
            var data = obj.data;
            if(obj.event === 'editMaintain'){
                editFunction(data);
            } 
        });
        //编辑维修级别
        var editFunction = function(data){
            var layerEditName = layer.open({
                type: 1 //Page层类型
                ,area: ['400px', '300px']
                ,title: '编辑维修级别'
                ,shade: 0.6 //遮罩透明度
                ,maxmin: true //允许全屏最小化
                ,anim: -1 //0-6的动画形式，-1不开启
                ,content: $("#editMaintainPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                ,cancel: function(){
                    //alert("关闭啦");
                }
                ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    $(".editMaintainPanelForm input[name='maintainLevel']").val(data.maintainLevel);
                    $(".editMaintainPanelForm input[name='maintainLevelName']").val(data.maintainLevelName);
                    $(".editMaintainPanelForm input[name='maintainHour']").val(data.maintainHour);
                    form.render();
                }
                ,btn: ['保存']
                ,btn1: function(index){
                	var param = {};
                	param["maintainLevel"] = $(".editMaintainPanelForm input[name='maintainLevel']").val();
                	param["maintainLevelName"] = $(".editMaintainPanelForm input[name='maintainLevelName']").val();
                	param["maintainHourStr"] = $(".editMaintainPanelForm input[name='maintainHour']").val();
                	layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/mainTainLevel/updateMainTainLevel",
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