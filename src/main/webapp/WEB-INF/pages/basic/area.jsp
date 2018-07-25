<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>区域名称</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="demoTable layui-search-form" id="areaPanel">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">区域名称</label>
				<div class="layui-input-inline">
					<input type="text" class="layui-input" id="areaName"
						name="areaName">
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="49"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm addButton" data-type="">新增</button>
				</cmc:button>
			</div>
		</div>

		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

		<span style="width: 100px"></span>
		<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
	</div>
	</div>
</body>
<script type="text/html" id="addAreaPanel">
    <form class="layui-form layerForm myLabelWidth125" action="">
        <div id="areaNameInput" class="layui-form-item">
            <label class="layui-form-label">区域名称<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="areaName1" id="areaName1" lay-verify="required" placeholder="此项为必填" autocomplete="off" class="layui-input">
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
            ,url:  basePath + '/area/pagingArea' //数据接口
            ,done : function(res, curr, count) {
            	layer.closeAll('loading'); //关闭加载中loading效果
            	form.render();
        	}
            ,cols: [[
               {field: 'areaName', title: '区域名称', sort: false} //, fixed: 'left'
              ,{field: 'createRealName', title: '创建人', width:150, sort: false}
              ,{field: 'createTime', title: '创建时间', width:200, sort: false
              ,templet: function(d){
                  if(d.createTime==null){
                      return "";
                  }else{
                      return  '<span>' +DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime)+'</span>';
                  }
              }
           }
              ,{field: 'createOrgName', title: '创建公司', width:180, sort: false}
              ,{field: 'isDefault', title: '默认入库区域', width:180, sort: false,templet: function(d){
                  if(d.isDefault == '0'){
                      return "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#xe618;</i>";
                  }else{
                	  var result = '';
                	  <cmc:button buttonId="50"> /* 用户有该按钮权限才会显示以下代码 */
                	  result = '<a class="layui-btn layui-btn-xs" lay-event="defaultArea">设置为默认入库区域</a>';
                      </cmc:button>
                      return result;
                  }
              }}
              ,{field: 'areaId', title: '区域ID', width:320, sort: false}
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
                	   'areaName':$(".layui-search-form").find("#areaName").val()
                   }
            };
          	if(currValue){
          		reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
          	}
        	table.reload('layuiReloadId', reloadParam);
        }
        layui.$('.queryButton').on('click', function(){
        	reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
        });
      //重置按钮
        layui.$('.demoTable .resetButton').on('click', function(){
        	  $("#areaPanel input[name='areaName']").val("");
        });
      //新增按钮点击
        layui.$('.demoTable  .addButton').on('click', function(){
            layerEditName = layer.open({
                type: 1 //Page层类型
                ,area: ['400px', '200px']
                ,title: '新增仓库区域'
                ,shade: 0.6 //遮罩透明度
                ,maxmin: true //允许全屏最小化
                ,anim: -1 //0-6的动画形式，-1不开启
                ,content: $("#addAreaPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                ,cancel: function(){
                    //alert("关闭啦");
                }
                ,btn: ['保存']
                ,btn1: function(index){
                    var areaName1 = $("#areaName1").val();
                    var param = {"areaName":areaName1};
                    layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/area/addArea",
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
            if(obj.event === 'defaultArea'){
                layer.confirm("确认要设置吗？", { title: "设置确认" }, function (index) {
                    var param = "areaId=" + data.areaId;
                    layer.load(2); //加载中loading效果
                    $.post(basePath + '/area/setDefaultArea', param, function (data) {
                    	layer.closeAll('loading'); //关闭加载中loading效果
                        layer.msg(data.msg);
                        if (data.status == 200) {
                            layer.close(index);
                            reloadTableData(); //重新加载table数据
                        }
                    });
                });
            }
        });
});
</script>
</html>