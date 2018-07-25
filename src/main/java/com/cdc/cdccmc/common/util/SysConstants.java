package com.cdc.cdccmc.common.util;
/** 
 * 常量类
 * @author ZhuWen
 * @date 2018-01-02
 */
public class SysConstants {
	/**
	 * session里存储登录用户时的key
	 */
	public static final String SESSION_USER = "t_system_user";
	/**
	 * session里存储门型添加的数据
	 */
	public static final String SESSION_DOORSCAN_ADD = "session_doorscan_add";
	/**
	 * session里存储门型对应到流转单明细中的EPCIDS数据
	 */
	public static final String SESSION_DOORSCAN_ADD_EPCIDS = "session_doorscan_add_epcIds";
	/**
	 * session里存储门型添加的数据所关联的流转单orderCode 回退时使用清除明细数据
	 */
	public static final String SESSION_DOORSCAN_ADD_ORDERCODE = "session_doorscan_add_ordercode";
	/**
	 * 本系统超级管理员：admin
	 */
	public static final String SUPER_ADMIN = "admin";
	/**
	 * XLSX
	 */
	public static final String XLSX = "xlsx";
	/**
	 * 整数0
	 */
	public static final Integer INTEGER_0 = 0;
	/**
	 * 整数1
	 */
	public static final Integer INTEGER_1 = 1;
	/**
	 * 整数2
	 */
	public static final Integer INTEGER_2 = 2;
	/**
	 * 字符串"0"
	 */
	public static final String STRING_0 = "0";
	/**
	 * 字符串"1"
	 */
	public static final String STRING_1 = "1";
	/**
	 * 字符串"2"
	 */
	public static final String STRING_2 = "2";
	/**
	 * 字符串"100"
	 */
	public static final String STRING_100 = "100";
	/**
	 * 批量插入每次为500条
	 */
	public static final Integer MAX_INSERT_NUMBER = 500;
	/**
	 * 批量导入时校验的最大错误行数。当超过这个数字时，不再继续校验文件数据。
	 */
	public static final Integer MAX_ERROR_ROWS = 3;
	/**
	 * 批量导入时允许最大数据行数。300行
	 */
	public static final Integer MAX_5000 = 5000;
	/**
	 * 批量导入时允许最大数据行数。 5000行
	 */
	public static final Integer MAX_UPLOAD_ROWS = 5000;
	/**
	 * 最大导出数据行数：20000条
	 */
	public static final Integer MAX_EXPORT_ROWS = 20000;
	/**
	 * excel导出分页查询数据行数：2000条
	 */
	public static final Integer MAX_QUERY_ROWS = 2000;
	/**
	 * 最大文件上传大小：2097152字节 = 2MB，这里的值必须是1024的倍数
	 */
	public static final Integer MAX_FILE_UPLOAD_SIZE = 2097152;
	/**
	 * 启用
	 */
	public static final String ACTIVE = "启用";
	/**
	 * 禁用
	 */
	public static final String DISABLE = "禁用";
	/**
	 * 是
	 */
	public static final String YES = "是";
	/**
	 * 否
	 */
	public static final String NO = "否";
	/**
	 * 不是
	 */
	public static final String NOT = "不是";
	/**
	 * 空字符串
	 */
	public static final String NULL_STR = "";
	/**
	 * 单引号
	 */
	public static final String DYH = "'";
	/**
	 * 逗号
	 */
	public static final String DH = ",";
	/**
	 * 右括号
	 */
	public static final String YKH = ")";
	/**
	 * 左括号
	 */
	public static final String ZKH = "(";
    /**
     * 字符d
     */
    public static final String STR_D = "d";
	/**
	 * 正则表达式匹配：器具代码
	 */
	public static final String REGEX_CONTAINER_CODE = "[\u4e00-\u9fa5_0-9a-zA-Z-]{1,30}";
	/**
	 * 正则表达式匹配：维修级别里的维修时间（小时）
	 */
	public static final String REGEX_MAINTAIN_LEVEL_HOUR = "[1-9]{1}[0-9]{0,5}";
	/**s
	 * 正则表达式匹配：器具代码类型
	 */
	public static final	String REGEX_CONTAINER_CODE_TYPE = "[0-9a-zA-Z]{1,9}";
	/**
	 * 正则表达式匹配：组织代码
	 */
	public static final	String REGEX_ORG_CODE = "[A-Z0-9]{2,7}";
	/**
	 * 正则表达式匹配：承运商管理中的联系电话
	 */
	public static final	String REGEX_SHIPPER_CONTAINER_NUMBER = "[\\s\\d-]{1,20}";
	/**
	 * 正则表达式匹配：身份证号码
	 * String 精准的表达式 = "([1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|([1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";
	 */
	public static final	String REGEX_ID_CARD_NUM = "(\\d{15})|(\\d{17}[0-9Xx]{1})";
	/**
	 * 正则表达式匹配：器具代码的在库数量
	 */
	public static final String REGEX_IN_ORG_NUM = "[0-9]{1,8}";

	/**
	 * 字符串："web收货"
	 */
	public static final String DEVICE_WEB = "web收货";
	/**
	 * 字符串："门型收货"
	 */
	public static final String DEVICE_DOOR = "门型收货";
	/**
	 * 字符串："app收货-实收入库"
	 */
	public static final String DEVICE_APP_ACTUAL = "app收货-实收入库";
	/**
	 * 字符串："app收货-照单全收"
	 */
	public static final String DEVICE_APP_ALL = "app收货-照单全收";
	/**
	 * 字符串："手持机收货-实收入库"
	 */
	public static final String DEVICE_HANDSET_ACTUAL = "手持机收货-实收入库";
	/**
	 * 字符串："手持机收货-照单全收"
	 */
	public static final String DEVICE_HANDSET_ALL = "手持机收货-照单全收";
	/**
	 * 字符串："门型扫描到器具，且器具未被流转单收货"
	 */
	public static final String DEVICE_DOOR_NOT_RECEIVE = "门型扫描到器具，且器具未被流转单收货";
	/**
	 * 字符串："打印并发货"
	 */
	public static final String CIRCULATE_REMARK_PRINT_AND_SEND = "打印并发货";
	/**
	 * 字符串："流转单收货详情页面处理收货差异：EPC覆盖。（新EPC入库）"
	 */
	public static final String CIRCULATE_REMARK_EPC_COVER_NEW = "流转单收货详情页面处理收货差异：EPC覆盖。（新EPC入库）";
	/**
	 * 字符串："流转单收货详情页面处理收货差异：EPC覆盖。（旧EPC返还）"
	 */
	public static final String CIRCULATE_REMARK_EPC_COVER_OLD = "流转单收货详情页面处理收货差异：EPC覆盖。（旧EPC返还）";
	/**
	 * 字符串："流转单收货详情页面处理收货差异：收货入库"
	 */
	public static final String CIRCULATE_REMARK_EPC_RECEIVE = "流转单收货详情页面处理收货差异：收货入库";
	/**
	 * 字符串："库内移位"
	 */
	public static final String CIRCULATE_REMARK_MOVE = "库内移位";
	/**
	 * 字符串："盘点单详情页面盘点差异处理：新增入库（扫描到新器具）"
	 */
	public static final String CIRCULATE_REMARK_INVENTORY_ADD_NEW = "盘点单详情页面盘点差异处理：新增入库（扫描到新器具）";
	/**
	 * 字符串："盘点单详情页面盘点差异处理：修正区域"
	 */
	public static final String CIRCULATE_REMARK_INVENTORY_MODIFY_AREA = "盘点单详情页面盘点差异处理：修正区域";
	/**
	 * 字符串："新增单个器具"
	 */
	public static final String CIRCULATE_REMARK_ADD_CONTAINER = "新增单个器具";
	/**
	 * 字符串："批量导入器具"
	 */
	public static final String CIRCULATE_REMARK_BATCH_ADD_CONTAINER = "批量导入器具";
	/**
	 * 字符串："生成新的采购入库单"
	 */
	public static final String CIRCULATE_REMARK_PURCHASE = "生成新的采购入库单";
	/**
	 * 字符串："门型收货"
	 */
	public static final String CIRCULATE_REMARK_DOOR_RECEIVE = "门型收货";
	/**
	 * 字符串："门型发货"
	 */
	public static final String CIRCULATE_REMARK_DOOR_SEND = "门型发货";
	/**
	 * 字符串："app端器具报修"
	 */
	public static final String CIRCULATE_REMARK_APP_APPLY_MAINTAIN = "APP-器具报修";
	/**
	 * 字符串："APP-流转单器具绑定（门型）-器具重绑-绑定"
	 */
	public static final String CIRCULATE_REMARK_APP_RE_BIND = "APP-流转单器具绑定（门型）-器具重绑-绑定";
	/**
	 * 字符串："APP-流转单器具绑定（门型）-器具重绑-解绑"
	 */
	public static final String CIRCULATE_REMARK_APP_RELIEVE_GROUP = "APP-流转单器具绑定（门型）-器具重绑-解绑";
	/**
	 * 字符串："器具组托"
	 */
	public static final String CIRCULATE_REMARK_ADD_GROUP = "器具组托";
	/**
	 * 字符串："器具解托"
	 */
	public static final String CIRCULATE_REMARK_RELIEVE_GROUP = "器具解托";
	/**
	 * 字符串："APP-器具组托-查看"
	 */
	public static final String CIRCULATE_REMARK_APP_CONTAINER_GROUP_SHOW = "APP-器具组托-查看";
	/**
	 * 手工单
	 */
	public static final String MANUAL_ORDER = "(手工单)";
	/**
	 * 字符串："批量初始化器具代码的库存数量"
	 */
	public static final String INIT_IN_ORG_NUMBER = "批量初始化器具代码的库存数量";
	
}

