package com.cdc.cdccmc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.dto.EpcSumDto;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/**
 * 收货
 * @author Clm
 * @date 2018-01-10
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class CirculateOrderReceiveService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(ContainerCodeService.class);

	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private CirculateOrderDeliveryService circulateOrderDeliveryService;

	@Value("#{sql['insertCirculateLatest']}")
	private String insertCirculateLatest;
	@Value("#{sql['deleteCirculateLatest']}")
	private String deleteCirculateLatest;
	@Value("#{sql['updateCirculateOrderIsReceive2']}")
	private String updateCirculateOrderIsReceive2;
	@Value("#{sql['queryCirculateDetail']}")
	private String queryCirculateDetail;

	/**
	 * 查询包装流转单主单和EPC明细信息，封装到ajaxBean给前端页面
	 * @param ajaxBean
	 * @param orderCode 包装流转单单号
	 * @return
	 */
	public AjaxBean queryCirculateOrderInfo(AjaxBean ajaxBean, String orderCode) {
		CirculateOrder order = circulateOrderService
				.queryCirculateOrderByOrderCode(orderCode);
		if (null == order) {
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("包装流转单单号" + StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		// 组装器具统计列表
		List<EpcSumDto> epcSumList = circulateOrderDeliveryService.buildEpcSumDtoByOrderCode(order);
		ajaxBean.setBean(order);
		ajaxBean.setList(epcSumList);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		return ajaxBean;
	}
}
