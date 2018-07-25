<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <title>手工流转单</title>
    <style type="text/css">
    </style>
</head>
<body>
<%------------------------------------------------
                        右边内容开始
    ------------------------------------------------%>

<div class="layui-form layui-search-form myLabelWidth120">
    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">手工流转单号</label>
            <div class="layui-input-inline">
                <input type="text" name="orderCodeSearch" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-inline">
            <button class="layui-btn layui-btn-sm queryButton" data-type="reload">查询</button>
            <button class="layui-btn layui-btn-sm resetButton_circulateOrder">重置</button>
        </div>
    </div>
</div>

<!-- 手工流转单列表 -->
<div style="text-align:center;font-weight:900">手工流转单列表</div>
<table class="layui-hide" id="LAY_TABLE_ORDER" lay-filter="myLayFilterOrder"></table>

<div class="demoTable layui-form layui-search-form">
    <div class="layui-form-item">
        <label class="layui-label" style="width:120px;">供应商：</label>
        <div class="layui-inline">
            <%=sessionUser.getCurrentSystemOrg().getOrgName() %>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-label" style="width:120px;">供应商代码：</label>
        <div class="layui-inline">
            <%=sessionUser.getCurrentSystemOrg().getOrgCode() %>
        </div>
        <label class="layui-label" style="width:120px;margin-left: 120px;">联系人：</label>
        <div class="layui-inline">
            <%=sessionUser.getCurrentSystemOrg().getContactName() == null ? "" : sessionUser.getCurrentSystemOrg().getContactName() %>
            <%=sessionUser.getCurrentSystemOrg().getContactPhone() == null ? "" : sessionUser.getCurrentSystemOrg().getContactPhone() %>
        </div>
    </div>
</div>
<div class="layui-form" action="">
    <div class="demoTable layui-form layui-search-form myLabelWidth120" id="purchaseDeliveryPanel">
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">编辑特殊备注</label>
                <div class="layui-input-inline">
                    <textarea name="specialDescription" placeholder="请输入内容" class="layui-textarea" style="width: 500px; "></textarea>
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">车牌号<span class="myRedColor">*</span></label>
                <div class="layui-input-inline">
                    <input type="text" class="layui-input" name="carNo" placeholder="" lay-verify="required">
                </div>
            </div>
            <div class="layui-inline">
                <label class="layui-form-label">司机姓名</label>
                <div class="layui-input-inline">
                    <input type="text" class="layui-input" name="driverName" placeholder="">
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">发货目标仓库<span class="myRedColor">*</span></label>
                <div class="layui-input-inline">
                    <select name="targetOrgId" lay-search="" lay-verify="required">
                        <option value="">直接选择或搜索选择</option>
                    </select>
                </div>
            </div>
            <div class="layui-inline">
                <label class="layui-form-label">司机联系方式</label>
                <div class="layui-input-inline">
                    <input type="text" class="layui-input" name="driverPhone" placeholder="">
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">出库类型<span class="myRedColor">*</span></label>
                <div class="layui-inline">
                    <input type="radio" name="tradeTypeCode" value="3" title="流转出库" lay-filter="radioFilter"
                           checked="checked">
                    <input type="radio" name="tradeTypeCode" value="4" title="维修出库" lay-filter="radioFilter">
                    <input type="radio" name="tradeTypeCode" value="5" title="租赁出库" lay-filter="radioFilter">
                    <input type="radio" name="tradeTypeCode" value="9" title="销售出库" lay-filter="radioFilter">
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label" style="width:120px;">Asn No.<span class="myRedColor">*</span></label>
                <div class="layui-input-inline">
                    <input type="text" class="layui-input" name="orderCode" placeholder="手工流转单号" lay-verify="required">
                </div>
            </div>
            <div class="layui-inline">
                <div class="layui-input-inline" style="margin-left: 50px;">
                    <cmc:button buttonId="78">
                    <button class="layui-btn layui-btn-sm" lay-submit="" lay-filter="commitCirculateOrderButton">提交主单
                    </button>
                    </cmc:button>
                    <button type="button" class="layui-btn layui-btn-primary layui-btn-sm clearCirculateOrderButton"
                            style="margin-left: 20px;">清空主单信息
                    </button>
                </div>
            </div>
        </div>
    </div>

    <table class="layui-table" id="circDetailTab" lay-filter="circDetailFilter"></table>
    <script type="text/html" id="handleBar">
        <%--<cmc:button buttonId="">--%>
        <a class="layui-btn layui-btn-xs" lay-event="delDetail">删除</a>
        <%--</cmc:button>--%>
    </script>

    <div class="layui-form-item">
        <div class="layui-input-block" style="width:600px; text-align: center;">
            <button type="button" class="layui-btn layui-btn layui-btn-normal layui-btn-sm addNewLineButton"
                    style="margin-right: 50px;">添加一行
            </button>
            <cmc:button buttonId="79">
            <button class="layui-btn layui-btn-sm" lay-submit="" lay-filter="commitCirculateDetailButton">提交器具明细
            </button>
            </cmc:button>
            <button class="layui-btn layui-btn-primary layui-btn-sm clearCirculateDetailButton"
                    style="margin-left: 50px;">清空器具明细
            </button>
        </div>
    </div>
</div>
<%------------------------------------------------
                    右边内容结束
------------------------------------------------%>
</body>
<script type="text/javascript">
    layui.use(['layer', 'table', 'element', 'form'], function () {
        var table = layui.table;
        var layer = layui.layer;
        var form = layui.form;
        var element = layui.element;
        initLeftmenu(element); //初始化左边菜单导航栏

        var orgList = []; //当前选中仓库的所有子公司列表，包括自己
        //初始化隶属公司下拉选择框
        $.ajax({
            url: basePath + "/org/listAllOrg",
            type: "POST",
            dataType: "json",
            data: {},
            success: function (result, textStatus, jqXHR) {
                var options = "<option value=\"\">直接选择或搜索选择</option>";
                var list = result.list;
                orgList = list; //赋值给全局变量
                $.each(list, function (i, obj) {
                    options += "<option value=\"" + obj.orgId + "\">" + obj.orgName + "</option>";
                });
                $("select[name='targetOrgId']").html(options);
                form.render();
            }
        });
        //初始化器具列表控件
        var tableCircIns = table.render({
            elem: '#circDetailTab'
            , width: 850
            , data: null
            , cols: [[
                {type: 'numbers',field:'sequenceNo', title: '序号', width: 60, sort: false},
                {field: 'containerCode', title: '器具代码', edit: 'text', width: 150, sort: false}
                , {field: 'containerTypeName', title: '器具类型', width: 160, sort: false}
                , {field: 'containerName', title: '器具名称', width: 160, sort: false}
                , {field: 'inOrgNumber', title: '库存数量', sort: false}
                , {field: 'sendNumber', title: '发货数量', edit: 'text', width: 100, sort: false}
                , {title: '操作', sort: false, align: 'center', width: 80, toolbar: '#handleBar'}
            ]],
            done: function (res, curr, count) {
            }
        });

        //“提交器具明细”按钮
        form.on('submit(commitCirculateDetailButton)', function (data) {
            var nowDt = table.cache["circDetailTab"];
            if (!nowDt || typeof(nowDt) == "undefined") {
                layer.msg("请添加信息！");
                return;
            }
            if (!checkCircDetailTab(nowDt[nowDt.length - 1])) {
                return;
            }
            console.log('nowDt',nowDt);
            //当点击删除时，会出现空[]
            var nowDt2 = [];
            var nowDt2Index = 1;
            for (var i = 0; i < nowDt.length; i++) {
                var vv = nowDt[i];
                if(!(vv instanceof Array)){
                    vv["sequenceNo"] = nowDt2Index++;
                    nowDt2.push(vv);
                }
            }
            // 请求参数
            var param = {};
            param['orderCode'] = $("input[name='orderCode']").val();
            param['circulateDetailList'] = nowDt2;
            // console.log(JSON.stringify(param));
            layer.load(2); //加载中loading效果
            $.ajax({
                url: basePath + "/circulateOrder/addCirculateDetailForAsn",
                contentType: 'application/json;charset=UTF-8',
                type: "POST",
                dataType: "json",
                data: JSON.stringify(param),
                success: function (result, textStatus, jqXHR) {
                    layer.closeAll('loading'); //关闭加载中loading效果
                    if (result.status == 200) {
                        layer.msg("保存成功！");
                    } else {
                        layer.alert(result.msg);
                    }
                }, error: function (index, upload) {
                    layer.closeAll('loading'); //关闭加载中loading效果
                }
            });
        });

        //“清空器具明细”按钮
        layui.$('.clearCirculateDetailButton').on('click', function () {
            layer.confirm("确认要清空器具明细信息吗？", {title: "提示信息"}, function (index) {
                table.reload('circDetailTab', {
                    data: null
                });
                table.cache["circDetailTab"] = null;
                layer.close(index);//关闭确认框
            });
            return false;
        });
        // 校验明细表
        var checkCircDetailTab = function (dtObj) {
            if (dtObj && dtObj.containerCode == '') {
                layer.msg("请输入器具代码！");
                return false;
            }
            if (dtObj && dtObj.sendNumber == '') {
                layer.msg("请输入发货数量！");
                return false;
            }
            if (dtObj && dtObj.sendNumber > dtObj.inOrgNumber) {
                layer.msg("发货数量不能大于库存数量！");
                return false;
            }
            if (dtObj && dtObj.sendNumber == 0) {
                layer.msg("发货数量必须大于0！");
                return false;
            }
            return true;
        }
        //"添加一行"按钮
        $('.addNewLineButton').on('click', function () {
            var newDt;
            var nowDt = table.cache["circDetailTab"];
            if (nowDt == null) {
                nowDt = [{
                    "containerCode": "",
                    "containerTypeName": "",
                    "containerName": "",
                    "sendNumber": ""
                }];
            } else {
                console.log("nowDt.length="+nowDt.length+"   nowDt["+(nowDt.length - 1)+"]="+nowDt[nowDt.length - 1]);
                if (!checkCircDetailTab(nowDt[nowDt.length - 1])) {
                    return;
                }
                newDt = {"containerCode": "", "containerTypeName": "", "containerName": "", "sendNumber": ""};
                nowDt.push(newDt);
            }
            table.reload('circDetailTab', {
                data: nowDt,
                limit: 100
            });
        });

        //监听“删除”
        table.on('tool(circDetailFilter)', function (obj) {
            if (obj.event === 'delDetail') {
                //删除该行
                obj.del();
                var nowDt = table.cache["circDetailTab"];
                //当点击删除时，会出现空[]
                var nowDt2 = [];
                for (var i = 0; i < nowDt.length; i++) {
                    var vv = nowDt[i];
                    if(!(vv instanceof Array)){
                        nowDt2.push(vv);
                    }
                }
                //重新渲染，以免序列号紊乱
                table.reload('circDetailTab', {
                    data: nowDt2
                });
            }
        });

        // 监听"器具代码"一栏输入值
        table.on('edit(circDetailFilter)', function (obj) {
            if (obj.field === 'containerCode') {
                // 输入重复校验
                var nowDt = table.cache["circDetailTab"];
                if (nowDt.length > 1) {
                    var isCheck = true;
                    $.each(nowDt, function (n, value) {
                        // 当前行不校验
                        if ((nowDt.length - 1) == n) {
                            return false
                        }
                        if (obj.data.containerCode == value.containerCode) {
                            layer.msg("器具代码不能重复，请重新输入！");
                            isCheck = false;
                            return false
                        }
                    });
                    if (!isCheck) {
                        $(this).val('');
                        return;
                    }
                }
                // 查询器具明细信息
                var _this = $(this);
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    // timeout: 5000, // 同步方式只能采用后台超时处理
                    async: false,   // 异步方式，回调不能清空单元格，遂采用同步
                    url: basePath + '/containerCode/queryContainerCodeAndInOrgNumber',
                    data: {'containerCode': obj.data.containerCode},
                    success: function (retData) {
                        if (retData.status != 200) {
                            layer.alert(retData.msg);
                            _this.val('');
                            return false;
                        }
                        var repDt = {
                            "containerCode": obj.data.containerCode,
                            "containerTypeName": retData.bean.inventoryHistory.containerTypeName,
                            "containerName": retData.bean.inventoryHistory.containerName,
                            "inOrgNumber": retData.bean.inventoryHistory.inOrgNumber,
                            "sendNumber": ""
                        };
                        var oldDts = table.cache["circDetailTab"];
                        oldDts[obj.data.LAY_TABLE_INDEX] = repDt;
                        table.reload('circDetailTab', {
                            data: oldDts
                        });
                    }
                })
            }
            if (obj.field === 'sendNumber') {
                if (!/^\d+$/.test(obj.value)) {
                    layer.msg("请输入正整数！");
                    $(this).val('');
                    return false;
                }
                if (obj.data.sendNumber > obj.data.inOrgNumber) {
                    layer.msg("发货数量不能大于库存数量！");
                    return false;
                }
                if (obj.data.sendNumber == 0) {
                    layer.msg("发货数量必须大于0！");
                    return false;
                }
            }
        });

        //“清空主单信息”按钮
        layui.$('.clearCirculateOrderButton').on('click', function () {
            layer.confirm("确认要清空流转单主单信息吗？", {title: "提示信息"}, function (index) {
                $(".layui-search-form input[name='carNo']").removeClass("layui-disabled");
                $(".layui-search-form input[name='carNo']").removeAttr("disabled");
                $(".layui-search-form input[name='carNo']").val("");
                $(".layui-search-form input[name='driverName']").removeClass("layui-disabled");
                $(".layui-search-form input[name='driverName']").removeAttr("disabled");
                $(".layui-search-form input[name='driverName']").val("");
                $(".layui-search-form select[name='targetOrgId']").removeClass("layui-disabled");
                $(".layui-search-form select[name='targetOrgId']").removeAttr("disabled");
                $(".layui-search-form select[name='targetOrgId']").val("");
                $(".layui-search-form input[name='driverPhone']").removeClass("layui-disabled");
                $(".layui-search-form input[name='driverPhone']").removeAttr("disabled");
                $(".layui-search-form input[name='driverPhone']").val("");
                $(".layui-search-form textarea[name='specialDescription']").removeClass("layui-disabled");
                $(".layui-search-form textarea[name='specialDescription']").removeAttr("disabled");
                $(".layui-search-form textarea[name='specialDescription']").val("");
                //Asn No. (手工流转单号)
                $(".layui-search-form input[name='orderCode']").removeClass("layui-disabled");
                $(".layui-search-form input[name='orderCode']").removeAttr("disabled", "disabled");
                $(".layui-search-form input[name='orderCode']").val("");
                //
                $("select[name='orgSelect']").val("");
                //交易类型
                $(".layui-search-form input[name='tradeTypeCode']").removeClass("layui-disabled");
                $(".layui-search-form input[name='tradeTypeCode']").removeAttr("disabled");
                setRaioValue_TradeTypeCode("3");  //设置为默认流转出库
                //解禁出库类型
                $(".layui-search-form input:radio[name='tradeTypeCode']").removeClass("layui-disabled");
                $(".layui-search-form input:radio[name='tradeTypeCode']").removeAttr("disabled");
                //清空包装流转单明细表和统计表
                var data = {"orderCode": ""};
                form.render();
                layer.close(index);//关闭确认框
            });
        });
        //设置"出库类型"单选框的值
        var setRaioValue_TradeTypeCode = function (setValue) {
            //出库类别单选框，设置值
            $(".layui-search-form input:radio[name='tradeTypeCode']").each(function () {
                if (this.value == setValue) {
                    this.checked = true; //设置选中值
                } else {
                    this.checked = false;
                }
            })
            form.render();
        }

        //“提交主单”按钮
        form.on('submit(commitCirculateOrderButton)', function (data) {
            var param = {};
            if (data.field.specialDescription.length > 300) {
                layer.msg("特殊备注最多300字符");
                return;
            }
            param['orderCode'] = data.field.orderCode;
            param['carNo'] = data.field.carNo;
            param['driverName'] = data.field.driverName;
            param['targetOrgId'] = data.field.targetOrgId;
            param['driverPhone'] = data.field.driverPhone;
            param['tradeTypeCode'] = data.field.tradeTypeCode;
            param['specialDescription'] = data.field.specialDescription;
            layer.load(2); //加载中loading效果
            $.ajax({
                url: basePath + "/circulateOrder/createCirculateOrderForAsn",
                type: "POST",
                dataType: "json",
                data: param,
                success: function (result, textStatus, jqXHR) {
                    layer.closeAll('loading'); //关闭加载中loading效果
                    if (result.status == 200) {
                        $(".layui-search-form input[name='orderCode']").addClass("layui-disabled");
                        $(".layui-search-form input[name='orderCode']").attr("disabled", "disabled");
                        //禁用表单
                        $(".layui-search-form input[name='carNo']").addClass("layui-disabled");
                        $(".layui-search-form input[name='carNo']").attr("disabled", "disabled");
                        $(".layui-search-form input[name='driverName']").addClass("layui-disabled");
                        $(".layui-search-form input[name='driverName']").attr("disabled", "disabled");
                        $(".layui-search-form select[name='targetOrgId']").addClass("layui-disabled");
                        $(".layui-search-form select[name='targetOrgId']").attr("disabled", "disabled");
                        $(".layui-search-form input[name='driverPhone']").addClass("layui-disabled");
                        $(".layui-search-form input[name='driverPhone']").attr("disabled", "disabled");
                        $(".layui-search-form input[name='tradeTypeCode']").addClass("layui-disabled");
                        $(".layui-search-form input[name='tradeTypeCode']").attr("disabled", "disabled");
                        $(".layui-search-form textarea[name='specialDescription']").addClass("layui-disabled");
                        $(".layui-search-form input[name='specialDescription']").attr("disabled", "disabled");
                        $(".demoTable .circulateOrderButton").addClass("layui-disabled");
                        $(".demoTable .circulateOrderButton").attr("disabled", "disabled");
                        form.render();
                        //重新加载列表
                        layui.$('.queryButton').click();
                    } else {
                        layer.alert(result.msg);
                    }
                }, error: function (index, upload) {
                    layer.closeAll('loading'); //关闭加载中loading效果
                }
            });
            return false;
        });

        layer.load(2); //加载中loading效果
        //方法级渲染
        table.render({
            elem: '#LAY_TABLE_ORDER'
            , loading: true
            , done: function (res, curr, count) {
                layer.closeAll('loading'); //关闭加载中loading效果
                form.render();
            }
            , url: basePath + '/circulateOrder/pagingManualCirculateOrderCurrentOrg' //包装流转单列表
            , cols: [[
                {field: 'orderCode', title: '包装流转单号', width: 180, sort: false} //, fixed: 'left'
                , {field: 'carNo', title: '车牌号', width: 130, sort: false}
                , {field: 'consignorOrgName', title: '发货仓库', width: 150, sort: false}
                , {field: 'targetOrgName', title: '收货仓库', width: 150, sort: false}
                , {field: 'tradeTypeName', title: '出库类别', width: 100, sort: false}
                , {
                    title: '操作', sort: false, align: 'center', width: 90, templet: function (d) {
                        if ((d.printNumber && d.printNumber > 0) || d.isReceive != '0') {
                            return "";
                        }
                        var result = '';
                        result += "<a class='layui-btn layui-btn-xs' lay-event='editOrder'>编辑</a>";
                        return result;
                    }
                }
                , {
                    field: 'creatTimeAndName', title: '操作日期', width: 180, height: 315
                    , templet: function (d) {
                        if (d.createTime) {
                            return '<span>' + DateUtil.timeToYYYY_MM_dd_hh_mm_ss(d.createTime) + '</span>';
                        }
                        return " ";
                    }
                }
                , {
                    field: 'isReceive', title: '收货状态', width: 140, height: 315
                    , templet: function (d) {
                        if (d.isReceive == '0') {
                            return '未收货';
                        }
                        if (d.isReceive == '1') {
                            return '已部分收货';
                        }
                        if (d.isReceive == '2') {
                            return '已全部收货';
                        }
                        return "" + d.isReceive;
                    }
                }
                , {
                    field: 'printNumber', title: '发货状态', width: 140, templet: function (d) {
                        if (d.printNumber && d.printNumber > 0) {
                            return '已发货';
                        }
                        return '未发货';
                    }
                }
                , {field: 'createRealName', title: '操作人', width: 130, sort: false}
                , {field: 'printNumber', title: '打印次数', width: 130, sort: false}
            ]]
            , id: 'layuiReloadId_circulate'
            , page: true
            , request: {
                pageName: 'currentPage' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            },
            response: {
                statusName: 'status' //数据状态的字段名称，默认：code
                , statusCode: 200 //成功的状态码，默认：0
                , msgName: 'msg', //状态信息的字段名称，默认：msg
                countName: 'total' //数据总数的字段名称，默认：count
                , dataName: 'data' //数据列表的字段名称，默认：data
            }
            , page: {
                elem: 'tablePaging', count: 0,
                first: '首页', last: '尾页', prev: '上一页', next: '下一页'
                , layout: ['count', 'limit', 'prev', 'page', 'next', 'skip'] //分页条按钮排序
                , curr: 1 //设定初始在第 1 页
                , limit: 3  //每页多少条
                , limits: [3, 10, 50] //支持每页数据条数选择
                , groups: 10 //显示 10 个连续页码
                , jump: function (obj) {
                }
            }  //开启分页

        });

        //点击“查询”按钮，加载包装流转单列表
        layui.$('.queryButton').on('click', function () {
            layer.load(2); //加载中loading效果
            table.reload('layuiReloadId_circulate', {  //查询包装流转单列表
                page: {curr: 1},  //重新从第 1 页开始
                where: {
                    'orderCode': $(".layui-search-form input[name='orderCodeSearch']").val()
                }
            });
        });

        //重置按钮
        layui.$('.resetButton_circulateOrder').on('click', function () {
            $(".layui-search-form input[name='orderCodeSearch']").val("");
        });

        //监听包装流转单列表里的“编辑”按钮
        table.on('tool(myLayFilterOrder)', function (obj) {
            var data = obj.data;
            if (obj.event === 'editOrder') { //"编辑"按钮点击
                editCirculateOrder(data);
            }
        });

        //编辑包装流转单
        var editCirculateOrder = function (data) {
            $(".layui-search-form input[name='carNo']").val(data.carNo);
            $(".layui-search-form input[name='carNo']").addClass("layui-disabled");
            $(".layui-search-form input[name='carNo']").attr("disabled", "disabled");
            $(".layui-search-form input[name='driverName']").val(data.driverName);
            $(".layui-search-form input[name='driverName']").addClass("layui-disabled");
            $(".layui-search-form input[name='driverName']").attr("disabled", "disabled");
            $(".layui-search-form select[name='targetOrgId']").val(data.targetOrgId);
            $(".layui-search-form select[name='targetOrgId']").addClass("layui-disabled");
            $(".layui-search-form select[name='targetOrgId']").attr("disabled", "disabled");
            $(".layui-search-form input[name='driverPhone']").val(data.driverPhone);
            $(".layui-search-form input[name='driverPhone']").addClass("layui-disabled");
            $(".layui-search-form input[name='driverPhone']").attr("disabled", "disabled");
            $(".layui-search-form input[name='orderCode']").val(data.orderCode);
            $(".layui-search-form input[name='orderCode']").addClass("layui-disabled");
            $(".layui-search-form input[name='orderCode']").attr("disabled", "disabled");
            //禁用出库类型
            $(".layui-search-form input:radio[name='tradeTypeCode']").addClass("layui-disabled");
            $(".layui-search-form input:radio[name='tradeTypeCode']").attr("disabled", "disabled");
            //禁用"生成包装流转单单号"按钮
            $(".layui-search-form .circulateOrderButton").addClass("layui-disabled");
            $(".layui-search-form .circulateOrderButton").attr("disabled", "disabled");

            setRaioValue_TradeTypeCode(data.tradeTypeCode);
            form.render();
            //加载和渲染包装流转单明细表
            renderCirculateDetail(data);
        }
        //请求器具明细接口/circulateOrder/queryCirculateDetail  3个参数：orderCode: 80000999;page: 1;limit: 100 获取到器具明细列表填充到table列表里
        var renderCirculateDetail = function (data) {
            layer.load(2); //加载中loading效果
            $.ajax({
                url: basePath + "/circulateOrder/queryCirculateDetail",
                type:"POST",
                dataType: "json",
                data: {"orderCode": data.orderCode,"page": 1,"limit": 200},
                success: function(result, textStatus, jqXHR){
                    layer.closeAll('loading'); //关闭加载中loading效果
                    layer.msg(result.msg);
                    if(result.status == 200){
                        layer.closeAll('page');   //成功后，关闭所有弹出框
                        //如果存在器具信息
                        if(result.list && result.list.length > 0){
                            var list = result.list;
                            var newData = [];
                            for (var i = 0; i < list.length; i++) {
                                newData[i] = {"containerCode":list[i]["containerCode"]
                                    ,"containerTypeName":list[i]["containerTypeName"]
                                    ,"containerName":list[i]["containerName"]
                                    ,"sendNumber":list[i]["sendNumber"]
                                    ,"sequenceNo":list[i]["sequenceNo"]
                                    ,"inOrgNumber":list[i]["inOrgNumber"]  };
                            }
                            table.reload('circDetailTab', {
                                data: newData
                            });
                        }else{
                            table.reload('circDetailTab', {
                                data: []
                            });
                        }
                    }
                },error: function(index, upload){
                    layer.closeAll('loading'); //关闭加载中loading效果
                }
            });
        };
    });
</script>
</html>