<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>车辆管理</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="demoTable layui-search-form myLabelWidth95">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:65px;">车牌号</label>
				<div class="layui-input-inline">
					<input type="text" name="carNo" lay-verify="required"
						autocomplete="on" class="layui-input">
				</div>
			</div>
			<div class="layui-inline">
				<label class="layui-form-label">承运商名称</label>
				<div class="layui-input-inline">
					<input type="text" name="shipperName" lay-verify="required"
						autocomplete="on" class="layui-input">
				</div>
			</div>
			<div class="layui-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
				<cmc:button buttonId="56"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm addButton" data-type="">新增</button>
				</cmc:button>
				<cmc:button buttonId="57"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button class="layui-btn layui-btn-sm" onclick="downloadTemplate('/car/carExcelDownload')">导入模版下载</button>
				</cmc:button>
				<cmc:button buttonId="58"> <!-- 用户有该按钮权限才会显示以下代码 -->
					<button type="button" class="layui-btn layui-btn-sm" id="uploadExcel">
					<i class="layui-icon"></i>批量导入
				</button>
				</cmc:button>
			</div>
		</div>
	</div>
		<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

	<script type="text/html" id="handleBar">
		<cmc:button buttonId="59">
        	<a class="layui-btn layui-btn-xs" lay-event="editCar">编辑</a>
		</cmc:button>
		<cmc:button buttonId="60">
        	<a class="layui-btn layui-btn-xs" lay-event="delCar">删除</a>
		</cmc:button>
    </script>



		<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
	</div>
	</div>
</body>
<!-- 新增车辆 -->
<script type="text/html" id="addCarPanel">
      <form class="layui-form addCarPanelForm myLabelWidth155" action="">
          <div class="layui-form-item">
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">车牌号<span class="myRedColor">*</span></label>
              <div class="layui-input-inline">
                  <input type="text" name="carNo" lay-verify="required" placeholder="此项必填" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">行驶证号码</label>
              <div class="layui-input-inline">
                  <input type="text" name="drivingLicense" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">行驶证有效期</label>
              <div class="layui-input-inline">
                  <input type="text" name="licenseValidDate" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item" >
              <label class="layui-form-label">隶属承运商名称</label>
              <div class="layui-input-inline">
                	<select name="shipperName"  lay-filter="selectNameFilter"  lay-search="">
                    	<option value=""></option>
                	</select>
              </div>
          </div>
      </form>
</script>
<!-- 编辑车辆 -->
<script type="text/html" id="editCarPanel">
      <form class="layui-form editCarPanelForm myLabelWidth155" action="">
          <div class="layui-form-item">
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">车牌号<span class="myRedColor">*</span></label>
              <div class="layui-input-inline">
                  <input type="text" name="carNo" lay-verify="required" placeholder="此项必填" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">行驶证号码</label>
              <div class="layui-input-inline">
                  <input type="text" name="drivingLicense" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item">
              <label class="layui-form-label">行驶证有效期</label>
              <div class="layui-input-inline">
                  <input type="text" name="licenseValidDate" placeholder="请输入" autocomplete="off" class="layui-input">
              </div>
          </div>
          <div class="layui-form-item" >
              <label class="layui-form-label">隶属承运商名称</label>
              <div class="layui-input-inline">
                	<select name="shipperName"  lay-filter="selectNameFilter"  lay-search="">
                    	<option value=""></option>
                	</select>
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

        upload.render({
            elem: '#uploadExcel'
            ,url: basePath+'/car/batchUploadCar'
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
            ,done : function(res, curr, count) {
            	layer.closeAll('loading'); //关闭加载中loading效果
        	}
            ,url:  basePath + '/car/pagingCar' //数据接口
            ,cols: [[
                {field: 'carNo', title: '车牌号', width:130, sort: false} //, fixed: 'left'
                ,{field: 'drivingLicense', title: '行驶证号码', width:190, sort: false}
                , {field: 'licenseValidDate', title: '行驶证有效期', width:130, sort: false
                    , templet: function (d) {
                        if (!d.licenseValidDate) {
                            return "";
                        } else {
                            return '<span>' + DateUtil.timeToYYYY_MM_dd(d.licenseValidDate) + '</span>';
                        }
                    }
                }
                ,{field: 'shipperName', title: '隶属承运商名称', width:180, sort: false}
                ,{ title: '操作', sort: false, align:'center', width:140, toolbar: '#handleBar'}
                ,{field:'creatTimeAndName', title: '创建人与创建时间', width: 250,height: 315
                    ,templet: function(obj){
                        var result = '';
    	    	  		if(obj.createRealName){
    	    	  			result += '<span class="tableCellPre">'+ obj.createRealName +'</span>';
    	    	  		}
    	    	  		if(obj.createTime){
    	    	  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.createTime) +'</span>';
    	    	  		}
    	    	  		return result;
                    }
                }
                ,{field:'updateTimeAndName', title: '修改人与修改时间', width: 250
                    ,templet: function(obj){
                        var result = '';
    	    	  		if(obj.modifyRealName){
    	    	  			result += '<span class="tableCellPre">'+ obj.modifyRealName +'</span>';
    	    	  		}
    	    	  		if(obj.modifyTime){
    	    	  			result += '<span class="tableCellAfter">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.modifyTime) +'</span>';
    	    	  		}
    	    	  		return result;
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
                	   'carNo':$(".layui-search-form input[name='carNo']").val(),
                       'shipperName':$(".layui-search-form input[name='shipperName']").val()
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
        	  $(".layui-search-form input[name='carNo']").val("");
        	  $(".layui-search-form input[name='shipperName']").val("");
        });
        var shippers;
        $.ajax({
            type: 'POST',
            url: basePath +'/shipper/listAllShipper',
            dataType:  'json',
            success: function(data){
                if(data.status == 200){
                    shippers = data.list;
                    form.render();
                }else {
                    layer.alert(data.msg);
                }
            }
        });

        var layerEditName;
        //新增按钮点击
        layui.$('.demoTable  .addButton').on('click', function(){
            layerEditName = layer.open({
                type: 1 //Page层类型
                ,area: ['500px', '400px']
                ,title: '新增车辆'
                ,shade: 0.6 //遮罩透明度
                ,maxmin: true //允许全屏最小化
                ,anim: -1 //0-6的动画形式，-1不开启
                ,content: $("#addCarPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                ,cancel: function(){
                    //alert("关闭啦");
                }
                ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    //日期
                    laydate.render({
                        elem: ".addCarPanelForm input[name='licenseValidDate']"
                    });

    	  		    var options = "<option value=\"\"></option>";
    		  		$.each( shippers, function(i, obj){
    		  			options +="<option value='" + obj.shipperId+"'>"+ obj.shipperName + "</option>";
    		  		});
                    $(".addCarPanelForm select[name='shipperName']").html(options);
                    form.render();
                }
                ,btn: ['保存']
                ,btn1: function(index){
                	var param = {};
                	var carNo = $(".addCarPanelForm input[name='carNo']").val();
                	param["carNo"] = carNo;
                	param["drivingLicense"] = $(".addCarPanelForm input[name='drivingLicense']").val();
                	param["licenseValidDateString"] = $(".addCarPanelForm input[name='licenseValidDate']").val();
                	param["shipperId"] = $(".addCarPanelForm select[name='shipperName']").val();
                	layer.load(2); //加载中loading效果
                	$.ajax({
                        url: basePath + "/car/addCar",
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
            if(obj.event === 'editCar'){
                editCarFunction(data);
            } else if(obj.event === 'delCar'){
                layer.confirm("确认要删除车牌号["+data.carNo+"]吗?", { title: "删除确认" }, function (index) {
                    var param = "carNo=" + data.carNo;
                    layer.load(2); //加载中loading效果
                    $.post(basePath + '/car/delCarByNo', param, function (data) {
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

        //编辑工种名称
        var editCarFunction = function(data){
            var layerEditName = layer.open({
                type: 1 //Page层类型
                ,area: ['500px', '400px']
                ,title: '车辆编辑'
                ,shade: 0.6 //遮罩透明度
                ,maxmin: true //允许全屏最小化
                ,anim: -1 //0-6的动画形式，-1不开启
                ,content: $("#editCarPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                ,cancel: function(){
                    //alert("关闭啦");
                }
                ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                    //日期
                    laydate.render({
                        elem: ".editCarPanelForm input[name='licenseValidDate']"
                    });
    	  		    var options = "<option value=\"\"></option>";
    		  		$.each( shippers, function(i, obj){
    		  			options +="<option value='" + obj.shipperId + "'>"+ obj.shipperName + "</option>";
    		  		});
                    $(".editCarPanelForm select[name='shipperName']").html(options);
                    $(".editCarPanelForm input[name='drivingLicense']").val(data.drivingLicense);
                    if(data.licenseValidDate){
                    	$(".editCarPanelForm input[name='licenseValidDate']").val(DateUtil.timeToYYYY_MM_dd(data.licenseValidDate));
                    }
                    $(".editCarPanelForm input[name='carNo']").val(data.carNo);
                    $(".editCarPanelForm input[name='carNo']").addClass("layui-disabled");
                    $(".editCarPanelForm input[name='carNo']").attr("disabled","disabled");
                    $(".editCarPanelForm select[name='shipperName']").val(data.shipperId);
                    form.render();
                }
                ,btn: ['保存']
                ,btn1: function(index){
                	var param = {};
                    param["carNo"] = data.carNo;
                    param["drivingLicense"] = $(".editCarPanelForm input[name='drivingLicense']").val();
                    param["licenseValidDateString"] = $(".editCarPanelForm input[name='licenseValidDate']").val();
                    param["shipperId"] = $(".editCarPanelForm select[name='shipperName']").val();
                    param["shipperName"] = $(".editCarPanelForm select[name='shipperName'] option:selected").text();
                    layer.load(2); //加载中loading效果
                    $.ajax({
                        url: basePath + "/car/updateCar",
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