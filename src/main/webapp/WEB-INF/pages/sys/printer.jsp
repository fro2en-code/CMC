<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>打印机管理</title>
<style type="text/css">
</style>
</head>
<body>
	<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>
	<div class="layui-form layui-search-form myLabelWidth80" action="">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label" style="width:85px;">仓库名称</label>
				<div class="layui-input-inline">
					<select name="targetOrgId" lay-search>
						<option value="">请选择</option>
					</select>
				</div>
				<div class="layui-input-inline">
				<button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
				</div>
				<div class="layui-input-inline">
					<cmc:button buttonId="84">
					<button class="layui-btn layui-btn-sm addButton" data-type="">新增</button>
					</cmc:button>
				</div>
			</div>
			<table class="layui-hide" id="LAY_TABLE" lay-filter="myLayFilter"></table>

			<script type="text/html" id="bar">
    </script>
			<%------------------------------------------------
                        右边内容结束
    ------------------------------------------------%>
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
        var orgListPage = null;
        initLeftmenu(element); //初始化左边菜单导航栏
		function getOrgOption(orgList,needUnknow)
		{
            var orgOption = "";
            if(needUnknow)
			{
			    orgOption = "<option value=''>直接选择或搜索选择</option>";
            }
            for (var i=0;i<orgList.length;i++)
            {
                /*
                if(curOrgId == orgList[i].orgId)
                {
                    orgOption += "<option value='" + orgList[i].orgId + "' selected>" + orgList[i].orgName+ "</option>";
                }
                else
                {
                    orgOption += "<option value='" + orgList[i].orgId + "'>" + orgList[i].orgName+ "</option>";
                }
                */
                orgOption += "<option value='" + orgList[i].orgId + "'>" + orgList[i].orgName+ "</option>";
            }
            return orgOption;
		}

        //初始化仓库select框
		$.ajax({
			type: 'POST',
			url: basePath + '/org/filialeSystemOrgList',
			dataType: 'json',
			success: function (data) {
				var orgList = data.list;
                orgListPage = orgList;
				$(".layui-search-form select[name='targetOrgId']").html(getOrgOption(orgList,true));
                //$(".layui-search-form select[name='targetOrgId']").val(curOrgId);
				form.render();
			}
		});

		//列表数据重载
		var reloadTableData = function(currValue){
			layer.load(2); //加载中loading效果
			var reloadParam = {
				where: {
					'orgId': $(".layui-search-form select[name='targetOrgId']").val()
				}
			};
			if(currValue){
				reloadParam["page"] = { curr : currValue}; //currValue：当前第几页
			}
			console.log('reloadParam',reloadParam);
			table.reload('layuiReloadId', reloadParam);
		}

        layer.load(2); //加载中loading效果
        //方法级渲染
       table.render({
            elem: '#LAY_TABLE'
            ,loading:true
            ,done : function(res, curr, count) {
            	layer.closeAll('loading'); //关闭加载中loading效果;
        	}
            ,url:  basePath + '/sysPrint/queryPrinter'
            ,cols: [[
	               		{field:'printCode',title: '打印机编码',width:120, sort: false}
		              ,{field: 'printName', title: '打印机名称', width:200, sort: false}
		              ,{field: 'orgName', title: '所属仓库', width:250, sort: false}
			   		  , {title: '操作', sort: false, align: 'center',  width: 180, templet:function (d) {
			   		      var result = '';
                   <cmc:button buttonId="85"> /* 用户有该按钮权限才会显示以下代码 */
                       result +='<a class="layui-btn layui-btn-xs editButton" lay-event="edit">编辑</a>';
                   </cmc:button>
                       //删除打印机会影响客户笔记本上部署的MQ队列监听,需要更新监听的队列名称,所以删除功能暂不放开,由后台统一处理数据.
                       //result +='<a class="layui-btn layui-btn-xs deleteButton" lay-event="delete">删除</a>';
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
       var tableReload = {
            reload: function(){  //执行重载
                table.reload('layuiReloadId', {
                    page: { curr: 1 },  //重新从第 1 页开始
                    where: {
                        'orgId': $(".layui-search-form select[name='targetOrgId']").val()
                    }
                });
            }
        };
        layui.$('.layui-search-form .queryButton').on('click', function(){
            reloadTableData(1);  //重新从第 1 页开始，重新加载table数据
        });



    //新增按钮点击
    layui.$('.addButton').on('click', function () {
        layerEditName = layer.open({
            type: 1 //Page层类型
            , area: ['800px', '300px']
            , title: '新增打印机'
            , shade: 0.6 //遮罩透明度
            , maxmin: true //允许全屏最小化
            , anim: -1 //0-6的动画形式，-1不开启
            , content: $("#addPrinterPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            , cancel: function () {
                //alert("关闭啦");
            }
            , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                if(''==$(".layui-search-form select[name='targetOrgId'] option:selected").val() || null==$(".layui-search-form select[name='targetOrgId'] option:selected").val())
				{
				    //alert("请先选择一个仓库");
				}
                //alert("选择仓库:"+$(".layui-search-form select[name='targetOrgId'] option:selected").text()+"   id="+$(".layui-search-form select[name='targetOrgId'] option:selected").val());
                $(".addPrinterPanelForm select[name='targetOrgId']").html(getOrgOption(orgListPage,false));
                var curOrgId = "<%= sessionUser.getCurrentSystemOrg().getOrgId()%>";
                $(".addPrinterPanelForm select[name='targetOrgId']").val(curOrgId);
                //$(".addPrinterPanelForm input[name='orgName']").val($(".layui-search-form select[name='targetOrgId'] option:selected").text());
                //$(".addPrinterPanelForm input[name='orgId']").val($(".layui-search-form select[name='targetOrgId'] option:selected").val());
                form.render();
            }
            , btn: ['保存']
            , btn1: function (index) {
                var param = {};
                //param['orgId'] = $(".addPrinterPanelForm input[name='orgId']").val();
                //param['orgName'] = $(".addPrinterPanelForm input[name='orgName']").val();
                param['orgId'] = $(".addPrinterPanelForm select[name='targetOrgId']").val();
                param['orgName'] = $(".addPrinterPanelForm select[name='targetOrgId'] option:selected").text();
				param['printName'] = $(".addPrinterPanelForm input[name='printName']").val();
                if (!param['printName']) {
                    layer.msg("打印机名称不能为空");
                    return;
                }

                layer.load(2); //加载中loading效果
                $.ajax({
                    url: basePath + "/sysPrint/addPrinter",
                    type: "POST",
                    dataType: "json",
                    data: param,
                    success: function (result, textStatus, jqXHR) {
                        layer.closeAll('loading'); //关闭加载中loading效果
                        layer.msg(result.msg);
                        if (result.status == 200) {
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
    table.on('tool(myLayFilter)', function (obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
            editJobNameFunction(data);
        } else if (obj.event === 'delete') {
            layer.confirm("确认要删除打印机:["+data.printName+"]吗?", { title: "删除确认" }, function (index) {
                var param = "printCode=" + data.printCode;
                layer.load(2); //加载中loading效果
                $.post(basePath + '/sysPrint/deletePrinter', param, function (data) {
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

    //编辑打印机名称
    var editJobNameFunction = function (data) {
        var layerEditName = layer.open({
            type: 1 //Page层类型
            , area: ['800px', '300px']
            , title: '编辑打印机名称'
            , shade: 0.6 //遮罩透明度
            , maxmin: true //允许全屏最小化
            , anim: -1 //0-6的动画形式，-1不开启
            , content: $("#addPrinterPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
            , cancel: function () {
                //alert("关闭啦");
            }
            , success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                /*
                if(''==$(".layui-search-form select[name='targetOrgId'] option:selected").val() || null==$(".layui-search-form select[name='targetOrgId'] option:selected").val())
                {
                    alert("请先选择一个仓库");
                }
                */
                //alert("选择仓库:"+$(".layui-search-form select[name='targetOrgId'] option:selected").text()+"   id="+$(".layui-search-form select[name='targetOrgId'] option:selected").val());
				//var orgOption = "<option value='" + data.orgId + "'>" + data.orgName+ "</option>";
                //$(".addPrinterPanelForm select[name='targetOrgId']").html(orgOption);
                $(".addPrinterPanelForm select[name='targetOrgId']").html(getOrgOption(orgListPage,false));
                $(".addPrinterPanelForm select[name='targetOrgId']").val(data.orgId);
                $(".addPrinterPanelForm select[name='targetOrgId']").attr("disabled",true);
                //$(".addPrinterPanelForm input[name='orgName']").val(data.orgName);
                //$(".addPrinterPanelForm input[name='orgId']").val(data.orgId);
                $(".addPrinterPanelForm input[name='printCode']").val(data.printCode);
                $(".addPrinterPanelForm input[name='printName']").val(data.printName);
                form.render();
            }
            , btn: ['保存']
            , btn1: function (index) {
                var param = {};
                param['printCode'] = $(".addPrinterPanelForm input[name='printCode']").val();
                //param['orgId'] = $(".addPrinterPanelForm input[name='orgId']").val();
                //param['orgName'] = $(".addPrinterPanelForm input[name='orgName']").val();
				param['orgId'] = $(".addPrinterPanelForm select[name='targetOrgId']").val();
                param['orgName'] = $(".addPrinterPanelForm select[name='targetOrgId'] option:selected").text();
                //alert("edit  select.text="+$(".addPrinterPanelForm select[name='targetOrgId'] option:selected").text()+"   param.orgName="+param['orgName']);
                param['printName'] = $(".addPrinterPanelForm input[name='printName']").val();
                if (!param['printName']) {
                    layer.msg("打印机名称不能为空");
                    return;
                }
                layer.load(2); //加载中loading效果
                $.ajax({
                    url: basePath + "/sysPrint/updatePrinter",
                    type: "POST",
                    dataType: "json",
                    data: param,
                    success: function (result, textStatus, jqXHR) {
                        layer.closeAll('loading'); //关闭加载中loading效果
                        layer.msg(result.msg);
                        if (result.status == 200) {
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
    //列表初始化
    var initTableData = function(){
        layer.load(2); //加载中loading效果
        var reloadParam = {
            where: {
                'orgId': ""
            }
        };
        reloadParam["page"] = { curr : 1}; //currValue：当前第几页
        console.log('reloadParam',reloadParam);
        table.reload('layuiReloadId', reloadParam);
    }
    initTableData();

});

</script>
<script type="text/html" id="addPrinterPanel">
	<div class="layui-form layerForm addPrinterPanelForm myLabelWidth125" action="">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">所属仓库<span class="myRedColor">*</span></label>
				<div class="layui-input-inline">
					<select name="targetOrgId" lay-search>
						<option value="">请选择</option>
					</select>
					<!--
					<input type="text" name="orgName" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled>
					<input type="hidden" name="orgId" placeholder="此项必填" autocomplete="off" class="layui-input">
					-->
				</div>
				<label class="layui-form-label">打印机名称<span class="myRedColor">*</span></label>
				<div class="layui-input-inline">
					<input type="text" name="printName" placeholder="此项必填" autocomplete="off" class="layui-input " >
					<input type="hidden" name="printCode" placeholder="此项必填" autocomplete="off" class="layui-input">

				</div>
			</div>
		</div>
	</div>
</script>
<script type="text/html" id="deletePrinterPanel">
	<div class="layui-form layerForm deletePrinterPanelForm myLabelWidth125" action="">
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">打印机名称<span class="myRedColor">*</span></label>
				<div class="layui-input-inline">
					<input type="text" name="printName" placeholder="此项必填" autocomplete="off" class="layui-input layui-disabled layui-bg-gray" disabled >
					<input type="hidden" name="printCode" placeholder="此项必填" autocomplete="off" class="layui-input">
				</div>
			</div>
		</div>
	</div>
</script>
</html>