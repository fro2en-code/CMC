package com.cdc.cdccmc.service.basic;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Car;
import com.cdc.cdccmc.domain.CarShipper;
import com.cdc.cdccmc.domain.ScrapWay;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.dto.CarDto;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 报废
 * 
 * @author ZhuWen
 * @date 2017-12-28
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class CarService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CarService.class);
	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private ShipperService shipperService;
	@Autowired
	private LogService logService;
	
	@Value("#{sql['addCar']}")
	private String addCar;
	@Value("#{sql['updateCar']}")
	private String updateCar;
	@Value("#{sql['deleteCar']}")
	private String deleteCar;
	@Value("#{sql['findCarByCarNo']}")
	private String findCarByCarNo;
	
	/**
	 * 车辆列表
	 * @param paging
	 * @param carDto
	 * @return
	 */        
	public Paging pagingCar(Paging paging,CarDto carDto) {
		// TODO Auto-generated method stub
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder(" SELECT  a.* from T_CAR a WHERE 1=1 ");
		if (StringUtils.isNotBlank(carDto.getShipperName())) {
			sql.append(" and a.shipper_name like :shipper_name ");
			paramMap.put("shipper_name", "%"+carDto.getShipperName()+"%");
		}
		if (StringUtils.isNotBlank(carDto.getCarNo())) {
			sql.append(" and a.car_no like :car_no ");
			paramMap.put("car_no", "%"+carDto.getCarNo()+"%");
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, CarDto.class);
		return resultPaging;
	}

	/**
	 * 添加车辆
	 * @param car
	 * @return
	 */
	public AjaxBean addCar(SystemUser sessionUser,AjaxBean ajaxBean,Car car) {
	    //如果承运商被选中
		if (StringUtils.isNotBlank(car.getShipperId())){
			CarShipper shipper = shipperService.findShipperById(car.getShipperId());
            if(null == shipper){
            	ajaxBean.setStatus(StatusCode.STATUS_311);
            	ajaxBean.setMsg("承运商"+StatusCode.STATUS_311_MSG);
            	return ajaxBean; 
            }
            car.setShipperName(shipper.getShipperName());
        }
        car.setCreateAccount(sessionUser.getAccount());
        car.setCreateRealName(sessionUser.getRealName());
		int result = this.namedJdbcTemplate.update(addCar, new BeanPropertySqlParameterSource(car));
		logService.addLogAccount(sessionUser, "新增车牌号["+car.getCarNo()+"]");
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 更新车辆
	 * @param car
	 * @return
	 */
	public  AjaxBean updateCar(SystemUser sessionUser,AjaxBean ajaxBean, Car car){
		car.setModifyAccount(sessionUser.getAccount());
		car.setModifyRealName(sessionUser.getRealName());
		//如果承运商被选中
		if (StringUtils.isNotBlank(car.getShipperId())){
			CarShipper shipper = shipperService.findShipperById(car.getShipperId());
            if(null == shipper){
            	ajaxBean.setStatus(StatusCode.STATUS_311);
            	ajaxBean.setMsg("承运商"+StatusCode.STATUS_311_MSG);
            	return ajaxBean; 
            }
            car.setShipperName(shipper.getShipperName());
        }
		int result = this.namedJdbcTemplate.update(updateCar, new BeanPropertySqlParameterSource(car));
		logService.addLogAccount(sessionUser, "更新车牌号为[" + car.getCarNo() + "]的车辆信息");
		return AjaxBean.returnAjaxResult(result);
    }

	/**
	 * 删除车辆
	 * @param carNo
	 * @return
	 */
	public AjaxBean delCar(SystemUser sessionUser,String carNo) {
		int result = jdbcTemplate.update(deleteCar, carNo);
		logService.addLogAccount(sessionUser, "删除车牌号["+carNo+"]的车辆");
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 批量插入车辆
	 * @param carList
	 */
	public void batchInsertCar(SystemUser sessionUser,ConcurrentLinkedQueue<Car> carList) {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(carList.toArray());
		namedJdbcTemplate.batchUpdate(addCar, params);
		logService.addLogAccount(sessionUser, "批量导入车辆号"+params.length+"条");
	}

	/**
	 * 根据车辆号码查询
	 * @param carNo
	 * @return
	 */
	public Car findCarByCarNo(String carNo) {
		List<Car> carList = jdbcTemplate.query(findCarByCarNo, new BeanPropertyRowMapper(Car.class), carNo);
		if(CollectionUtils.isEmpty(carList)){
			return null;
		}
		return carList.get(0);
	}
	/**
	 * 查询车辆是否存在
	 * @return
	 */
	public List<Car> ListAllCar() {
		String sql = "select * from T_CAR ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Car.class));
	}

	/**
	 * 处理批量导入车辆
	 * @param sessionUser 当前登录用户
	 * @param ajaxBean
	 * @param file 批量导入文件
	 * @return
	 */
	public AjaxBean batchUploadCar(SystemUser sessionUser, AjaxBean ajaxBean,File file) {
		//组装所有器具代码为map格式，key=shipperName, value=CarShipper对象
		List<CarShipper> allShipperList = shipperService.listAllShipper();
		ConcurrentHashMap<String, CarShipper> shipperMap = new ConcurrentHashMap<String, CarShipper>();
		for(CarShipper shipper : allShipperList){
			shipperMap.put(shipper.getShipperName(), shipper);
		}
		//数据校验
		ajaxBean = validExcelData(file.getPath(),shipperMap,sessionUser);
		//如果校验不通过，直接返回
		if(ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		ConcurrentLinkedQueue<Car> carList = new ConcurrentLinkedQueue<>();
		//校验通过,批量查询对应的运营商id,并构建map数组
		List<Map<Integer, String>> excelList = ajaxBean.getList();
		for (int i = 0; i < excelList.size(); i++) {
			if(null == excelList.get(i)){  //如果是空行，直接跳过
				continue;
			}
			Car car = new Car();
		    car.setCarNo(StringUtils.trim(excelList.get(i).get(0)));
		    car.setDrivingLicense(StringUtils.trim(excelList.get(i).get(1)));
		    try {
		    	if (StringUtils.isNotBlank(excelList.get(i).get(2))){
					car.setLicenseValidDate(DateUtil.parseToDate(StringUtils.trim(excelList.get(i).get(2)),DateUtil.yyyy_MM_dd));
				}
			} catch (ParseException e) {
				LOG.info(e.getMessage(),e);
				logService.addLogError(sessionUser, e, e.getMessage(), null);
			}
		    car.setCreateAccount(sessionUser.getAccount());
		    car.setCreateRealName(sessionUser.getRealName());
		    if (StringUtils.isNotBlank(excelList.get(i).get(3))){
				//根据承运商名称获取承运商对象
				CarShipper shipper = shipperMap.get(excelList.get(i).get(3));
				car.setShipperName(shipper.getShipperName());
				car.setShipperId(shipper.getShipperId());
			}
            //添加车辆到新增队列
		    carList.add(car);

			//如果达到批量插入条数，就进行批量插入
			if(carList.size() >= SysConstants.MAX_INSERT_NUMBER){
				batchInsertCar(sessionUser,carList);
				carList.clear();
			}
        }
		//如果还有需要批量插入的数据
		if(carList.size() > SysConstants.INTEGER_0){
			batchInsertCar(sessionUser,carList);
			carList.clear();
		}
		return ajaxBean;
	}
	
	/**
	 * 检测上传 excel的内容
	 * @param path 上传的文件存储路径
	 * @param sessionUser
	 * @return
	 */
	private AjaxBean validExcelData(String path, ConcurrentHashMap<String, CarShipper> shipperMap, SystemUser sessionUser){
		AjaxBean ajaxBean = new AjaxBean();
		List<Map<Integer, String>> mapList = null;
		try {
			mapList = ExcelUtil.readExcel(path,4);
		} catch (Exception e) { //文件批量导入失败！
			LOG.error(StatusCode.STATUS_402_MSG, e);
			logService.addLogError(sessionUser, e, StatusCode.STATUS_402_MSG, null);
			ajaxBean.setStatus(StatusCode.STATUS_402);
			ajaxBean.setMsg(StatusCode.STATUS_402_MSG);
			return ajaxBean;
		}
		if(mapList.size() == 0){ //如果数据一行都没有，未检测到需导入数据，请检查上传文件内数据输入是否正确。
			LOG.error(StatusCode.STATUS_403_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_403);
			ajaxBean.setMsg(StatusCode.STATUS_403_MSG);
			return ajaxBean;
		}
		if(mapList.size() > SysConstants.MAX_UPLOAD_ROWS){ //文件数据行数超过上限多少行
			LOG.error(StatusCode.STATUS_401_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_401);
			ajaxBean.setMsg(StatusCode.STATUS_401_MSG);
			return ajaxBean;
		}

		List<String> errList = new ArrayList<String>();
		LOG.info("本次需批量导入"+mapList.size()+"条车辆数据");
		
		int row = 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		for(Map<Integer, String> map : mapList){
			row = row + 1;
			if(null == map){  //如果是空行，直接跳过
				continue;
			}
		    String carNo = map.get(0);
            if(StringUtils.isBlank(carNo)){
				errList.add("第"+row+"行，车牌号不能为空。");
            } else if(carNo.length() > 15){
    			errList.add("第"+row+"行，车牌号长度不能超过15个字符。");
            } else{
            	Car findCar = findCarByCarNo(carNo);
            	if(null != findCar){
    				errList.add("第"+row+"行，车牌号["+carNo+"]已存在，不能新增。");
            	}else{
					int row2 = 1;
                	two:for(Map<Integer, String> map2 : mapList){
	            		row2 = row2 + 1;
                		if(null != map2 && map.get(0).equals(map2.get(0)) && row != row2){ //如果excel内发现重复车牌号
            				errList.add("第"+row+"行，车牌号["+carNo+"]在excel内重复，请核查。");
                			break two;
                		}
                	}
            	}
            }
            
            if(StringUtils.isNotBlank(map.get(1)) && map.get(1).length() > 20){ //如果行驶证号码非空
     			errList.add("第"+row+"行，行驶证号码不能超过20个字符。");
            }

            //行驶证有效日期
            String licenseValidDate = map.get(2);
            if(StringUtils.isNotBlank(licenseValidDate)){
            	try {
                    sdf.parse(licenseValidDate);
                    DateUtil.parseToTimestamp(licenseValidDate, DateUtil.yyyy_MM_dd);
                }catch (ParseException e3){
         			errList.add("第"+row+"行，行驶证有效日期["+licenseValidDate+"]必须符合yyyy-MM-dd(例如：2017-12-24)日期格式。");
                }
            }

           if(StringUtils.isNotBlank(map.get(3)) && !shipperMap.containsKey(map.get(3))){ //如果承运商名称不为空，并且该承运商不存在
    			errList.add("第"+row+"行，找不到隶属承运商["+map.get(3)+"]信息，请至承运商管理页面先添加该承运商信息。");
           }
		}
		if (errList.size() > 0) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setList(errList);
		} else {
			ajaxBean.setList(mapList);
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		}
		return ajaxBean;
	}
}
