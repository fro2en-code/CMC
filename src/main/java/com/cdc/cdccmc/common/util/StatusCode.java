package com.cdc.cdccmc.common.util;

import java.math.BigDecimal;

/** 
 * 状态代码，状态文本信息
 * @author ZhuWen
 * @date 2018-01-08
 */
public class StatusCode {

	public static final Integer STATUS_200 = 200;
	public static final String STATUS_200_MSG = "操作成功！";
	
	public static final Integer STATUS_201 = 201;
	public static final String STATUS_201_MSG = "未知错误";

	/************************************************
	 *                 权限类错误
	 ************************************************/
	public static final Integer STATUS_100 = 100;
	public static final String STATUS_100_MSG = "检测到用户未登录，请重新登录！";
	public static final Integer STATUS_101 = 101;
	public static final String STATUS_101_MSG = "用户不属于任何仓库或其他组织机构,请联系管理员";
	public static final Integer STATUS_102 = 102;
	public static final String STATUS_102_MSG = "用户没有选择任何仓库";
	public static final Integer STATUS_103 = 103;
	public static final String STATUS_103_MSG = "当前用户无当前选中仓库的任何菜单权限，请联系管理员！或切换至另一个仓库。";
	public static final Integer STATUS_104 = 104;
	public static final String STATUS_104_MSG = "用户名或密码错误";
	public static final Integer STATUS_105 = 105;
	public static final String STATUS_105_MSG = "用户必须隶属于至少一个仓库或组织机构，请联系管理员！";
	public static final Integer STATUS_106 = 106;
	public static final String STATUS_106_MSG = "超级管理员账号["+SysConstants.SUPER_ADMIN+"]权限不可变更！请选择其他账号！";
	public static final Integer STATUS_107 = 107;
	public static final String STATUS_107_MSG = "旧密码错误，请重试。";
	public static final Integer STATUS_108 = 108;
	public static final String STATUS_108_MSG = "账号已存在，请重试。";
	public static final Integer STATUS_109 = 109;
	public static final String STATUS_109_MSG = "当前账号被禁用，请联系管理员。";	
	public static final Integer STATUS_110 = 110;
	public static final String STATUS_110_MSG = "账号不存在，请重试。";
	public static final Integer STATUS_111 = 111;
	public static final String STATUS_111_MSG = "此账号非门型账号，不能登陆门型设备。";
	public static final Integer STATUS_112 = 112;
	public static final String STATUS_112_MSG = "此账号为门型账号，登陆失败！";
	/************************************************
	 *                 数据类错误
	 ************************************************/
	public static final Integer STATUS_300 = 300;
	public static final String STATUS_300_MSG = "查无数据";
	public static final Integer STATUS_301 = 301;
	public static final String STATUS_301_MSG = "数据格式不正确";
	public static final Integer STATUS_302 = 302;
	public static final String STATUS_302_MSG = "数据已存在";
	public static final Integer STATUS_303 = 303;
	public static final String STATUS_303_MSG = "数据已存在业务流转记录,不能删除";
	public static final Integer STATUS_304 = 304;
	public static final String STATUS_304_MSG = "被编辑对象已被更新，请刷新后重新尝试编辑！";
	public static final Integer STATUS_305 = 305;
	public static final String STATUS_305_MSG = "不能为空";
	public static final Integer STATUS_306 = 306;
	public static final String STATUS_306_MSG = "数据新增失败";
	public static final Integer STATUS_307 = 307;
	public static final String STATUS_307_MSG = "数据编辑失败";
	public static final Integer STATUS_308 = 308;
	public static final String STATUS_308_MSG = "数据删除失败";
	public static final Integer STATUS_309 = 309;
	public static final String STATUS_309_MSG = "数据更新失败";
	public static final Integer STATUS_310 = 310;
	public static final String STATUS_310_MSG = "数据选择有误，请重新选择！";
	public static final Integer STATUS_311 = 311;
	public static final String STATUS_311_MSG = "数据不存在！请核查！";
	public static final Integer STATUS_312 = 312;
	public static final String STATUS_312_MSG = "器具不在当前仓库！请核查！";
	public static final Integer STATUS_313 = 313;
	public static final String STATUS_313_MSG = "包装流转单配送目的地仓库不能是当前仓库！";
	public static final Integer STATUS_314 = 314;
	public static final String STATUS_314_MSG = "出库类型和目的地仓库类型必须匹配！";
	public static final Integer STATUS_315 = 315;
	public static final String STATUS_315_MSG = "包装流转单配送目的地仓库不是当前仓库！不能进行器具入库操作！";
	public static final Integer STATUS_316 = 316;
	public static final String STATUS_316_MSG = "包装流转单已入库目的地仓库！不能再次进行入库操作！";
	public static final Integer STATUS_317 = 317;
	public static final String STATUS_317_MSG = "默认入库区域不存在，请先设置一个默认入库区域。";
	public static final Integer STATUS_318 = 318;
	public static final String STATUS_318_MSG = "查询结果有误！";
	public static final Integer STATUS_319 = 319;
	public static final String STATUS_319_MSG = "该仓库已存在未盘点完毕的盘点单号";
	public static final Integer STATUS_320 = 320;
	public static final String STATUS_320_MSG = "不存在正在盘点中的盘点单，请在web端核查！";
	public static final Integer STATUS_321 = 321;
	public static final String STATUS_321_MSG = "盘点单已经完毕！";
	public static final Integer STATUS_322 = 322;
	public static final String STATUS_322_MSG = "编辑对象主键ID缺失！编辑失败！";
	public static final Integer STATUS_323 = 323;
	public static final String STATUS_323_MSG = "盘点单结束失败！请重新尝试";
	public static final Integer STATUS_324 = 324;
	public static final String STATUS_324_MSG = "器具已经过时，不能再次执行过时操作。";
	public static final Integer STATUS_325 = 325;
	public static final String STATUS_325_MSG = "器具代码应为1到30位数字或字母或中文字符、横杠的任意组合。";
	public static final Integer STATUS_326 = 326;
	public static final String STATUS_326_MSG = "器具已经在库维修,不能进行重复报修。";
	public static final Integer STATUS_327 = 327;
	public static final String STATUS_327_MSG = "器具已经出库维修,不能进行重复报修。";
	public static final Integer STATUS_328 = 328;
	public static final String STATUS_328_MSG = "器具已进行报废申请,不能进行重复报修。";
	public static final Integer STATUS_329 = 329;
	public static final String STATUS_329_MSG = "器具已报废,不能进行报修申请";
	public static final Integer STATUS_330 = 330;
	public static final String STATUS_330_MSG = "器具类型新增失败。";
	public static final Integer STATUS_331 = 331;
	public static final String STATUS_331_MSG = "器具代码新增失败。";
	public static final Integer STATUS_332 = 332;
	public static final String STATUS_332_MSG = "包装流转单已打印发货，不能删除任何EPC器具。";
	public static final Integer STATUS_333 = 333;
	public static final String STATUS_333_MSG = "包装流转单数据不存在！请核查！";
	public static final Integer STATUS_334 = 334;
	public static final String STATUS_334_MSG = "包装入库单器具明细不能为空！";
	public static final Integer STATUS_335 = 335;
	public static final String STATUS_335_MSG = "发货方不能是当前仓库！";
	public static final Integer STATUS_336 = 336;
	public static final String STATUS_336_MSG = "包装入库单创建失败！";
	public static final Integer STATUS_337 = 337;
	public static final String STATUS_337_MSG = "器具添加失败！添加到流转单的器具必须是当前仓库下的器具，并且流转状态为在库状态！";
	public static final Integer STATUS_338 = 338;
	public static final String STATUS_338_MSG = "包装流转单已部分收货，或已全部收货，不能进行整单照单全收！";
	public static final Integer STATUS_339 = 339;
	public static final String STATUS_339_MSG = "包装流转单器具明细为空，不能进行打印并出库！";
	public static final Integer STATUS_340 = 340;
	public static final String STATUS_340_MSG = "器具代码被禁用，请重新选择！";
	public static final Integer STATUS_341 = 341;
	public static final String STATUS_341_MSG = "包装入库单器具明细为空，不能进行打印！";
	public static final Integer STATUS_342 = 342;
	public static final String STATUS_342_MSG = "器具明细列表为空，不能进行收货操作！";
	public static final Integer STATUS_343 = 343;
	public static final String STATUS_343_MSG = "器具不存在于器具列表，也不存在于采购预备表，请核查！";
	public static final Integer STATUS_344 = 344;
	public static final String STATUS_344_MSG = "新增器具流转差异失败";
	public static final Integer STATUS_345 = 345;
	public static final String STATUS_345_MSG = "新增器具流转差异成功";
	public static final Integer STATUS_346 = 346;
	public static final String STATUS_346_MSG = "app端新增包装流转单失败";
	public static final Integer STATUS_347 = 347;
	public static final String STATUS_347_MSG = "app端新增包装流转单成功";
	public static final Integer STATUS_348 = 348;
	public static final String STATUS_348_MSG = "包装流转单已全部收货成功，不能重复收货！";
	public static final Integer STATUS_349 = 349;
	public static final String STATUS_349_MSG = "托盘盖已入库，不能进行重复入库！";
	public static final Integer STATUS_350 = 350;
	public static final String STATUS_350_MSG = "该器具不存在于仓库：";
	public static final Integer STATUS_351 = 351;
	public static final String STATUS_351_MSG = "流转单差异处理仓库必须是流转单收货仓库";
	public static final Integer STATUS_352 = 352;
	public static final String STATUS_352_MSG = "该仓库下没有菜单权限，请重新选择其他仓库。";
	public static final Integer STATUS_353 = 353;
	public static final String STATUS_353_MSG = "盘点差异处理仓库必须是盘点仓库";
	public static final Integer STATUS_354 = 354;
	public static final String STATUS_354_MSG = "包装流转单尚未发货，不能进行收货！";
	public static final Integer STATUS_355 = 355;
	public static final String STATUS_355_MSG = "该盘点单已盘点完毕！";
	public static final Integer STATUS_356 = 356;
	public static final String STATUS_356_MSG = "必须是包装流转单发货仓库才可以删除包装流转单的器具！";
	public static final Integer STATUS_357 = 357;
	public static final String STATUS_357_MSG = "必须是包装流转单发货仓库才允许打印包装流转单！";
	public static final Integer STATUS_358 = 358;
	public static final String STATUS_358_MSG = "没有关联到任何包装流转单！";
	public static final Integer STATUS_359 = 359;
	public static final String STATUS_359_MSG = "包装流转单已打印发货（打印次数大于0），不能再添加器具！";
	public static final Integer STATUS_360 = 360;
	public static final String STATUS_360_MSG = "该器具或托盘未组托！无相关组托明细。";
	public static final Integer STATUS_361 = 361;
	public static final String STATUS_361_MSG = "解托失败!该器具未组托!";
	public static final Integer STATUS_362 = 362;
	public static final String STATUS_362_MSG = "请提交有效的epc扫描数据";
	public static final Integer STATUS_363 = 363;
	public static final String STATUS_363_MSG = "已人工收货，不能再次进行收货。";
	public static final Integer STATUS_364 = 364;
	public static final String STATUS_364_MSG = "必须是包装入库单创建仓库，才能打印该包装入库单。";
	public static final Integer STATUS_365 = 365;
	public static final String STATUS_365_MSG = "流转单已作废，不能打印！";
	public static final Integer STATUS_366 = 366;
	public static final String STATUS_366_MSG = "流转单已发货，不能作废！";
	public static final Integer STATUS_367 = 367;
	public static final String STATUS_367_MSG = "打印机连接不稳定,请稍候重试!";
	public static final Integer STATUS_368 = 368;
	public static final String STATUS_368_MSG = "门型账号只能隶属于一个仓库";
	public static final Integer STATUS_369 = 369;
	public static final String STATUS_369_MSG = "打印失败！原因：";
	public static final Integer STATUS_370 = 370;
	public static final String STATUS_370_MSG = "两个EPC必须是相同的器具代码才能做EPC覆盖处理";


	/************************************************
	 *                 文件上传下载类错误
	 ************************************************/ 
	public static final Integer STATUS_400 = 400;
	public static final String STATUS_400_MSG = "文件上传至服务器失败";
	public static final Integer STATUS_401 = 401;
	public static final String STATUS_401_MSG = "文件数据行数超过上限"+SysConstants.MAX_UPLOAD_ROWS + "行";
	public static final Integer STATUS_402 = 402;
	public static final String STATUS_402_MSG = "文件批量导入失败！";
	public static final Integer STATUS_403 = 403;
	public static final String STATUS_403_MSG = "未检测到需导入数据，请检查上传文件内数据输入是否正确。";
	public static final Integer STATUS_404 = 404;
	public static final String STATUS_404_MSG = "文件下载失败！模版不存在";
	public static final Integer STATUS_405 = 405;
	public static final String STATUS_405_MSG = "文件格式不是.xlsx";
	public static final Integer STATUS_406 = 406;
	public static final String STATUS_406_MSG = "response获取输出流失败！";
	public static final Integer STATUS_407 = 407;
	public static final String STATUS_407_MSG = "response关闭输出流失败！";
	public static final Integer STATUS_408 = 408;
	public static final String STATUS_408_MSG = "XSSFWorkbook对象关闭流失败！";
	public static final Integer STATUS_409 = 409;
	public static final String STATUS_409_MSG = "excel文件上传过大，不能超过：" 
			+ new BigDecimal(SysConstants.MAX_FILE_UPLOAD_SIZE).divide(new BigDecimal(1024 * 1024)) + "MB";
}
