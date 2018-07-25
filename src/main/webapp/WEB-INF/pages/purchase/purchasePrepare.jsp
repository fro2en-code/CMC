<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>采购预备表</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="layui-form layui-search-form" id="purchasePreparePanel">
		<div class="layui-form-item">
			<div class="layui-inline">
				<div class="layui-inline">
					<div class="layui-inline">
						<label class="layui-form-label" style="width: 100px">收货开始日期</label>
						<div class="layui-input-inline">
							<input type="text" class="layui-input" name="startDate">
						</div>
					</div>
					<div class="layui-inline">
						<label class="layui-form-label" style="width: 100px">收货结束日期</label>
						<div class="layui-input-inline">
							<input type="text" class="layui-input" name="endDate">
						</div>
					</div>
					<div class="layui-inline">
						<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
						<button type="button" class="layui-btn layui-btn-sm resetButton">重置</button>
						<cmc:button buttonId="22"> <!-- 用户有该按钮权限才会显示以下代码 -->
							<button class="layui-btn layui-btn-sm" onclick="downloadTemplate('/purchase/excelDownload')">导入模版下载</button>
						</cmc:button>
						<cmc:button buttonId="23"> <!-- 用户有该按钮权限才会显示以下代码 -->
							<button type="button" class="layui-btn layui-btn-sm " id="uploadExcel">
							<i class="layui-icon"></i>批量导入
							</button>
						</cmc:button>
				</div>
			</div>
		</div>
	</div>
	
	<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>
	
	<div class="layui-form layui-search-form" action="">
		<cmc:button buttonId="24"> <!-- 用户有该按钮权限才会显示以下代码 -->
		<button class="layui-btn layui-btn-sm addButton"
						data-type="reload">增加到新采购入库单</button>
		</cmc:button>
	</div>
		
	<div class="layui-row layui-col-space18">
	  <div class="layui-col-md5">
			<%------------------------------------------------
		                       左边表格开始
		    ------------------------------------------------%>
		   <!--  <table class="layui-hide" id="LAY_TABLE_left" lay-filter="myLayFilterLeft"></table> -->
		    <div style="text-align: center;font-weight: 900">器具明细表</div>
		    <table class="layui-table">
				<colgroup>
					<col width="80">
					<col >
					<col>
					<col width="90">
				</colgroup>
				<thead>
					<tr>
						<th>序号</th>
						<th>器具代码</th>
						<th>EPC编号</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody id="myTableBodyLeft"></tbody>
			</table>
			<%------------------------------------------------
		                        左边表格结束
		    ------------------------------------------------%>
	  </div>
	  <div class="layui-col-md7">
			<%------------------------------------------------
		                       右边表格开始
		    ------------------------------------------------%>
		   <!--  <table class="layui-hide" id="LAY_TABLE_right" lay-filter="myLayFilterRight"></table> -->
		    <div style="text-align: center;font-weight: 900">采购入库单概况</div>
		    <table class="layui-table">
				<colgroup>
					<col width="90">
					<col >
					<col >
					<col width="150">
				</colgroup>
				<thead>
					<tr>
						<th>序号</th>
						<th>器具代码</th>
						<th>器具名称</th>
						<th>数量</th>
					</tr>
				</thead>
				<tbody id="myTableBodyRight"></tbody>
			</table>
			
			<div class="layui-form" action="">
				<div class="layui-form-item">
					<label class="layui-form-label" style="width: 180px;">送货方<span class="myRedColor">*</span></label>
					<div class="layui-input-inline">
						<select name="orgSelect" lay-search="">
							<option value="">直接选择或搜索选择</option>
						</select>
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label" style="width: 180px;">入库备注</label>
					<div class="layui-input-inline">
						<input type="text" class="layui-input" name="inOrgRemark">
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label" style="width: 180px;">新采购入库单号:</label>
					<div class="layui-input-inline">
						<label class="layui-form-label" style="width: 250px;" id="purchaseInOrgMainId"></label>
					</div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label" style="width: 180px;"></label>
					<div class="layui-input-inline">
						<cmc:button buttonId="25"> <!-- 用户有该按钮权限才会显示以下代码 -->
						<button class="layui-btn layui-btn-sm createButton" data-type="reload">生成新采购入库单</button>
						</cmc:button>
						<button class="layui-btn layui-btn-sm resetCreateButton" data-type="reload">重置</button>
					</div>
				</div>
			</div>
			<%------------------------------------------------
		                        右边表格结束
		    ------------------------------------------------%>
	  </div>
	</div>
		<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
	</div>
	</div>
	</div>
</body>

<script type="text/javascript">
layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element','form','layedit'], function(){
        var table = layui.table;
        var layer = layui.layer;
        var form = layui.form;
        var upload = layui.upload;
        var laydate = layui.laydate;
        var element = layui.element;
        initLeftmenu(element); //初始化左边菜单导航栏
        
        //时间选择器
        laydate.render({
           elem: ".layui-search-form input[name='startDate']"
           ,type: 'datetime'
           ,min: '2018-01-01'
         });
        //时间选择器
        laydate.render({
           elem: ".layui-search-form input[name='endDate']"
           ,type: 'datetime'
           ,min: '2018-01-01'
         });
        
      //初始化隶属公司下拉选择框
  	  $.ajax({
  	  	  url: basePath + "/org/listAllOrg",
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
      
	  //“批量上传”按钮渲染
      upload.render({
          elem: '#uploadExcel'
          ,url: basePath+'/purchase/batchUpload'
          ,accept: 'file' //普通文件
          ,before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
          	layer.load(2); //加载中loading效果
          }
          ,done: function(result){
          	  layer.closeAll('loading'); //关闭加载中loading效果
              if(result.status == 200){
              		layer.msg('批量导入成功！'); 
                    tableReload.reload(); //重新加载table数据
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
  	  var myLayTable = table.render({
  	    elem: '#LAY_TABLE'
  	    ,loading:true 
        ,done : function(res, curr, count) {
        	layer.closeAll('loading'); //关闭加载中loading效果
    	}
  	    ,url:  basePath + '/purchase/pagingPurchasePrepare' //数据接口
  	    ,cols: [[
  		      { width:40,title: '<input type="checkbox" name="epcCheckBoxAll" lay-filter="epcCheckBoxLayFilterAll" lay-skin="primary">'
  		    	  ,templet: function(d){
		  	    		  if(!(d.isReceive == 0 || d.purchaseInOrgMainId)){ //如果未入库，或者采购入库单号不为空
			      	  		  return '<input type="checkbox" name="epcCheckBox" lay-filter="epcCheckBoxLayFilter" lay-skin="primary"  '
	      	  		            +' value="' + d.epcId + '" epcId="'+d.epcId+'" containerCode="'+d.containerCode+'" containerName="'+d.containerName+'" '+ '>';
		  	    		  }
		  	    		  return '';
  		      }}
  		   	  ,{field: 'epcId', title: 'EPC编号', width:180}
	  	      ,{field: 'printCode', title: '印刷编号', width:150}
	  	      ,{field: 'containerCode', title: '器具代码', width:120}
	          ,{field: 'isReceive', title: '是否已收货', width:100, sort: false
		      	  ,templet:function(d){
		      		  if(d.isReceive == 0){ //收货状态。0未收货，1已收货
		      			  return "未收货";
		      		  }else{
		      			  return "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#xe618;</i>";
		      		  }
	        	  }
	         }
		        ,{field: 'receiveTime', title: '收货时间与收货人', width:180, sort: false
			      	,templet:function(obj){
			  	  		var result = '';
				  		if(obj.receiveRealName){
				  			result += '<span class="tableCellPre">'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(obj.receiveTime) +'</span>';
				  		}
				  		if(obj.receiveTime){
				  			result += '<span class="tableCellAfter">'+ obj.receiveRealName +'</span>';
				  		}
			        	return result;
			      	}  
		        }
	  	    ,{field: 'purchaseInOrgMainId', title: '采购入库单单号', width:170}
	        ,{field: 'inOrgTime', title: '入库时间', width:180, sort: false
		      	,templet:function(d){
		      		if(d.inOrgTime==null){
		      			return "";
		      		}else{
		      			return '<span>'+ DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.inOrgTime)+'<span>';
		      		}
		      	}  
	        }
	  	      ,{field: 'containerTypeName', title: '器具类型', width:100}
	  	      ,{field: 'containerName', title: '器具名称', width:200}
	  	      ,{field: 'containerSpecification', title: '规格', width:100}
	  	      ,{field: 'containerTexture', title: '材质'}
  		   	  ,{field: 'epcType', title: 'EPC类型', width:180}
  		   	  ,{title: '创建仓库', field: 'createOrgName', width:180}
	  	    , {field: 'creatTimeAndName', title: '创建人与创建时间', width: 250
	            , templet: function (d) {
	                if (d.createTime == null && d.createRealName == null) {
	                    return "";
	                } else {
	                    return '<span>' + d.createRealName + '</span>&nbsp;<span>' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime) + '</span>';
	                }
	            }
	        }
		   	  ,{title: '收货仓库', field: 'receiveOrgName', width:180}
	  	    ,{field: 'isAloneGroup', title: '是否单独成托', width: 120,templet:function(d){
	      		  if(d.isAloneGroup == 0){ //是否单独成托，0不是，1是
	      			  return "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#x1006;</i>";
	      		  }else{
	      			  return "<i class='layui-icon' style='font-size: 30px; color: #1E9FFF;'>&#xe618;</i>";
	      		  }
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
  		    } 
  	  });

	  //监听复选框点击事件
	  form.on('checkbox(epcCheckBoxLayFilter)', function(data){
		   if(!data.elem.checked){  //如果未选中
			   $("input:checkbox[name='epcCheckBoxAll']").each(function () { 
		            this.checked = false; 
		       }) 
		   }
		   form.render();
	  }); 

	  //监听"全选"复选框点击事件
	  form.on('checkbox(epcCheckBoxLayFilterAll)', function(data){
		   if(data.elem.checked){ //如果选中
			   $("input:checkbox[name='epcCheckBox']").each(function () { 
				   this.checked = true; 
		       }) 
		   }else{ //如果未选中
			   $("input:checkbox[name='epcCheckBox']").each(function () { 
		            this.checked = false; 
		       }) 
		   }
		   form.render();
	  }); 
        //列表数据重载
        var tableReload = {
            reload: function(){  //执行重载
                table.reload('layuiReloadId', {
                    page: { curr: 1 },  //重新从第 1 页开始
                    where: {
                    	'startDate':$(".layui-search-form input[name='startDate']").val()
                        ,'endDate':$(".layui-search-form input[name='endDate']").val()
                    }
                });
            }
        };
        //点击“查询”按钮
        layui.$('.queryButton').on('click', function(){
        	layer.load(2); //加载中loading效果
        	tableReload.reload();
        });
      //“入库开始日期和结束日期的重置按钮”
        layui.$('.resetButton').on('click', function(){
        	  $(".layui-form input[name='startDate']").val("");
        	  $(".layui-form input[name='endDate']").val("");
        });
        var epcIdArr = []; //器具明细表
        var epcIdSum = []; //采购入库单概况
        //点击“增加到新采购入库单”按钮
        layui.$('.layui-search-form .addButton').on('click', function(){
        		$("input:checkbox[name='epcCheckBox']").each(function () { 
        			var obj = {};
        			obj["epcId"] = $(this).attr("epcId");
        			obj["containerCode"] = $(this).attr("containerCode");
        			obj["containerName"] = $(this).attr("containerName");
				   if(this.checked){ //如果是选中状态
		        		var findFlag = false;
			        	for (var i = 0; i < epcIdArr.length; i++) {
			        		var epcId = epcIdArr[i]["epcId"];
		        			if(epcId == obj["epcId"]){
		        				findFlag = true;
		        				return;
		        			}
			        	}
			        	if(!findFlag){ //如果在器具明细表里面没有，那么添加，否则不重复添加
			        		epcIdArr.push(obj);
			        	}
				   }
		       }) 
		       //组装两个表的数据：器具明细表、采购入库单概况
		       buildTalbeData(epcIdArr);
        });

        //组装两个表的数据：器具明细表、采购入库单概况
        var buildTalbeData = function(epcIdArr){
    	    //组装器具明细表
    		var tbodyHtmlLeft = "";
    		for (var i = 0; i < epcIdArr.length; i++) {
    			var obj = epcIdArr[i];
    			tbodyHtmlLeft += "<tr><td>"+(i+1)+"</td>";
    			tbodyHtmlLeft += "<td>"+obj.containerCode+"</td>";
    			tbodyHtmlLeft += "<td>"+obj.epcId+"</td>";
    			tbodyHtmlLeft += "<td><i epcId='"+obj.epcId+"' class='layui-icon my-delete-icon' style='font-size: 22px; color: #1E9FFF;cursor:pointer;'>&#xe640;</i>  </td></tr>";
			}
    		$("#myTableBodyLeft").html(tbodyHtmlLeft);
    		
		    epcIdSum = []; //清空统计对象
    		for (var i = 0; i < epcIdArr.length; i++) {
    			var obj = epcIdArr[i];
    			var find = false;
    			for (var k = 0; k < epcIdSum.length; k++) {
    				var obj2 = epcIdSum[k];
    				if(obj2.containerCode == obj.containerCode){
    					find = true;
    					obj2["sendNumber"] = obj2.sendNumber + 1;  //如果找到了，器具代码统计数量+1
    					epcIdSum.splice(k,1,obj2); //将下标为k的元素替换为新的obj2，只替换一个下标对象
    				}
    			}
    			if(!find){ //如果没找到，初始化该器具代码数量为1
    				var obj2 = {};
    				obj2["containerCode"] = obj.containerCode;
    				obj2["containerName"] = obj.containerName;
    				obj2["sendNumber"] = 1;
    				epcIdSum.push(obj2);
    			}
			}

    	    //组装采购入库单概况
    		var tbodyHtmlRight = "";
    		for (var i = 0; i < epcIdSum.length; i++) {
    			var obj = epcIdSum[i];
    			tbodyHtmlRight += "<tr><td>"+(i+1)+"</td>";
    			tbodyHtmlRight += "<td>"+obj.containerCode+"</td>";
    			tbodyHtmlRight += "<td>"+obj.containerName+"</td>";
    			tbodyHtmlRight += "<td>"+obj.sendNumber+"</td></tr>";
			}
    		$("#myTableBodyRight").html(tbodyHtmlRight);
    		table.render(); //重新渲染表格
    		form.render(); //重新渲染表单，因为表格里有表单
    		
     	    //点击“删除”图标
     	    layui.$('.my-delete-icon').on('click', function(){
     	    	 	var thisEpcId = $(this).attr("epcId");
     	    		for (var k = 0; k < epcIdArr.length; k++) {
     	    			if(epcIdArr[k]["epcId"] == thisEpcId){
     	    				epcIdArr.splice(k,1); //将下标为k的元素删除
	       	  		        //组装两个表的数据：器具明细表、采购入库单概况
	       	  		        buildTalbeData(epcIdArr);
     	    				break;
     	    			}
     	    		}
     	     });
        }
        
        //点击“重置”按钮。“生成新采购入库单”按钮旁边的那个
        layui.$('.layui-form .resetCreateButton').on('click', function(){
        	//清空“器具明细表”
        	epcIdArr = [];
        	$("#myTableBodyLeft").html("");
        	//清空“采购入库单概况”
        	epcIdSum = [];
    		$("#myTableBodyRight").html("");
    		//清空表单
    		$(".layui-form select[name='orgSelect']").val("");
    		$(".layui-form input[name='inOrgRemark']").val("");
    		$("#purchaseInOrgMainId").html("");
    		form.render();
        });
        
        //点击“生成新采购入库单”按钮
        layui.$('.layui-form .createButton').on('click', function(){
        	if(epcIdArr.length <= 0){
        		layer.msg("器具明细表不能为空！");
        		return;
        	}
        	var param = {};
        	var epcIdList = [];
        	for (var k = 0; k < epcIdArr.length; k++) {
        		epcIdList.push(epcIdArr[k]["epcId"]);
        	}
        	param["epcIdList"] = epcIdList;
        	param["consignorOrgId"] = $(".layui-form select[name='orgSelect']").val();
        	param["inOrgRemark"] = $(".layui-form input[name='inOrgRemark']").val();
        	layer.load(2); //加载中loading效果
        	$.ajax({
        	  	  url: basePath + "/purchase/createPurchaseInOrg",
        	  	  type:"POST",
        	  	  dataType: "json",
        	  	  data: param,
        	  	  success: function(result, textStatus, jqXHR){
		            	layer.closeAll('loading'); //关闭加载中loading效果
		            	if(result.status == 200){
		            		layer.msg('操作成功！');
	        	    		$("#purchaseInOrgMainId").html(result.bean.purchaseInOrgMainId);
	        	    		//刷新采购入库单列表
	        	        	layer.load(2); //加载中loading效果
	        	        	tableReload.reload();
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