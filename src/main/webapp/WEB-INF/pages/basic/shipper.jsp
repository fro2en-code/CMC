<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>承运商管理</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                     右边内容开始
 ------------------------------------------------%>
	<div class="demoTable layui-search-form myLabelWidth90" id="shipperNamePanel">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">承运商名称</label>
				<div class="layui-input-inline">
					<input type="text" name="shipperNameSeach" id="shipperNameSeach"
						lay-verify="required" autocomplete="on" class="layui-input">
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="53"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm addButton" data-type="insert">新增</button>
				</cmc:button>
			</div>
		</div>
		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
		<script type="text/html" id="handleBar">
			<cmc:button buttonId="54">
        		<a class="layui-btn layui-btn-xs" lay-event="editShipper">编辑</a>
			</cmc:button>
			<cmc:button buttonId="55">
        		<a class="layui-btn layui-btn-xs" lay-event="delShipper">删除</a>
			</cmc:button>
    	</script>
		<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
	</div>
	</div>
</body>
<!-- 新增弹出框 -->
<script type="text/html" id="addCarPanel">
      <form class="layui-form layerForm addCarPanelForm myLabelWidth115" action="">
          <div id="carNoDivInsert" class="layui-form-item">
              <label class="layui-form-label">承运商名称<span class="myRedColor">*</span></label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperName" lay-verify="required" placeholder="此项必填" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">地址</label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperAddress" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">联系人姓名</label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperContactName" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item" >
              <label class="layui-form-label">联系电话</label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperContactNumber" lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
      </form>
</script>
<!-- 编辑弹出框 -->
<script type="text/html" id="editCarPanel">
      <form class="layui-form layerForm editCarPanelForm myLabelWidth115" action="">
          <div id="carNoDivInsert" class="layui-form-item">
              <label class="layui-form-label">承运商名称<span class="myRedColor">*</span></label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperName" lay-verify="required" autocomplete="off" class="layui-input layui-disabled" disabled="disabled">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">地址</label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperAddress" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">联系人姓名</label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperContactName" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item" >
              <label class="layui-form-label">联系电话</label>
              <div class="layui-input-inline">
                  <input type="text" name="shipperContactNumber" lay-verify="required" placeholder="请输入" autocomplete="off" class="layui-input">
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
          var element = layui.element;
          initLeftmenu(element); //初始化左边菜单导航栏

    	  layer.load(2); //加载中loading效果
          //方法级渲染
          table.render({
              elem: '#LAY_TABLE'
              ,loading:true
              ,done: function(result){
              		layer.closeAll('loading'); //关闭加载中loading效果
              }
              ,url:  basePath + '/shipper/pagingCarShipper' //数据接口
              ,cols: [[
                  {field: 'shipperName', title: '承运商名称', width:250,sort: false} //, fixed: 'left'
                  ,{field: 'shipperAddress', title: '地址', width:200, sort: false}
                  ,{field: 'shipperContactName', title: '联系人姓名', width:120, sort: false}
                  ,{field: 'shipperContactNumber', title: '联系电话', width:160, sort: false}
                  ,{ title: '操作', sort: false, align:'center', width:120, toolbar: '#handleBar'}
                  ,{field:'creatTimeAndName', title: '创建人与创建时间', width: 250,height: 315
                      ,templet: function(d){
                          if(d.createTime==null&&d.createRealName == null){
                              return "";
                          }else{
                              return  '<span>'+d.createRealName+'</span>&nbsp;<span>' +d.createTime+'</span>';
                          }
                      }
                  }
                  ,{field:'updateTimeAndName', title: '修改人与修改时间', width: 250
                      ,templet: function(d){
                          if(d.modifyTime==null&&d.modifyRealName == null){
                              return "";
                          }else{
                              return  d.modifyRealName+'&nbsp;' +d.modifyTime;
                          }
                      }
                  }
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
                    	 'shipperName':$(".layui-search-form").find("#shipperNameSeach").val()
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
          	  $("#shipperNamePanel input[name='shipperNameSeach']").val("");
          });

          var layerEditName;
          //新增按钮点击
          layui.$('.demoTable  .addButton').on('click', function(){
              layerEditName = layer.open({
                  type: 1 //Page层类型
                  ,area: ['500px', '400px']
                  ,title: '新增承运商'
                  ,shade: 0.6 //遮罩透明度
                  ,maxmin: true //允许全屏最小化
                  ,anim: -1 //0-6的动画形式，-1不开启
                  ,content: $("#addCarPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                  ,cancel: function(){
                      //alert("关闭啦");
                  }
                  ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                      form.render();
                  }
                  ,btn: ['保存']
                  ,btn1: function(index){
                	  var param = {};
                	  param["shipperName"] = $(".addCarPanelForm input[name='shipperName']").val();
                	  param["shipperAddress"] = $(".addCarPanelForm input[name='shipperAddress']").val();
                	  param["shipperContactName"] = $(".addCarPanelForm input[name='shipperContactName']").val();
                	  param["shipperContactNumber"] = $(".addCarPanelForm input[name='shipperContactNumber']").val();
                      layer.load(2); //加载中loading效果
                      $.ajax({
                          url: basePath + "/shipper/addCarShipper",
                          type:"POST",
                          dataType: "json",
                          data: param,
                          success: function(result, textStatus, jqXHR){
                              layer.msg(result.msg);
                              if(result.status == 200){
                                  layer.closeAll('page');   //成功后，关闭所有弹出框
                                  reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
                              }
                              layer.closeAll('loading'); //关闭加载中loading效果
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
              if(obj.event === 'editShipper'){  //编辑
                  editJobNameFunction(data); 
              } else if(obj.event === 'delShipper'){ //删除
				  //弹出框确认是否删除？	
            	  layer.confirm('是否删除承运商['+data.shipperName+']?', {icon: 3, title:'提示'}, function(index){
            		      //点击弹出框上的确定按钮，进行以下业务处理
	            		  var param = "shipperId="+data.shipperId;
	            		  layer.load(2); //加载中loading效果
	                      $.post(basePath+'/shipper/delCarShipper',param,function(data){
	                    	  layer.closeAll('loading'); //关闭加载中loading效果
	                          layer.msg(data.msg);
	                          if(data.status == 200){
	                        	  reloadTableData(); //重新加载table数据
	                          }
	                      });
	                      layer.close(index);  //关闭当前询问窗口。
            	  });
                  
              }
          });

          //编辑承运商
          var editJobNameFunction = function(data){
              var layerEditName = layer.open({
                  type: 1 //Page层类型
                  ,area: ['500px', '400px']
                  ,title: '编辑承运商'
                  ,shade: 0.6 //遮罩透明度
                  ,maxmin: true //允许全屏最小化
                  ,anim: -1 //0-6的动画形式，-1不开启
                  ,content: $("#editCarPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                  ,cancel: function(){
                      //alert("关闭啦");
                  }
                  ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                      form.render();
                      $(".editCarPanelForm input[name='shipperName']").val(data.shipperName);
                      $(".editCarPanelForm input[name='shipperAddress']").val(data.shipperAddress);
                      $(".editCarPanelForm input[name='shipperContactName']").val(data.shipperContactName);
                      $(".editCarPanelForm input[name='shipperContactNumber']").val(data.shipperContactNumber);
                  }
                  ,btn: ['保存']
                  ,btn1: function(index){
                	  var param = {};
                	  param["shipperId"] = data.shipperId;
                	  param["shipperName"] = $(".editCarPanelForm input[name='shipperName']").val();
                	  param["shipperAddress"] = $(".editCarPanelForm input[name='shipperAddress']").val();
                	  param["shipperContactName"] = $(".editCarPanelForm input[name='shipperContactName']").val();
                	  param["shipperContactNumber"] = $(".editCarPanelForm input[name='shipperContactNumber']").val();
                      if (!param["shipperId"]){
                          layer.msg("承运商ID为空");
                          return;
                      }
                      layer.load(2); //加载中loading效果
                      $.ajax({
                          url: basePath + "/shipper/updateCarShipper",
                          type:"POST",
                          dataType: "json",
                          data: param,
                          success: function(result, textStatus, jqXHR){
                              layer.msg(result.msg);
                              if(result.status == 200){
                                  layer.closeAll('page');   //成功后，关闭所有弹出框
                                  reloadTableData(); //重新加载table数据
                              }
                              layer.closeAll('loading'); //关闭加载中loading效果
					  	  },error: function(index, upload){
				            	layer.closeAll('loading'); //关闭加载中loading效果
				          }
                      });
                  }
              });
          }
});
</script>
</body>
</html>