package com.cdc.cdccmc.runnable;

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.DoorEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.ContextLoader;

import java.sql.Timestamp;
import java.util.List;


@Service
public class ASyncTask {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ASyncTask.class);

    @Autowired
    private DoorEquipmentService doorEquipmentService;
    @Autowired
    private CirculateService circulateService;

    @Transactional(propagation = Propagation.NESTED)
    @Async
    public void saveScanReceiveByThread(SystemUser sessionUser, List<Container> containerList, Timestamp createTime)
    {
        LOG.info("---saveScanReceiveByThread  异步执行 ");
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        LOG.info("---ContextLoader.getCurrentWebApplicationContext()="+ContextLoader.getCurrentWebApplicationContext());
//        PlatformTransactionManager txManager = ContextLoader.getCurrentWebApplicationContext().getBean(PlatformTransactionManager.class);
//        TransactionStatus status = txManager.getTransaction(def);
        try {
            doorEquipmentService.saveScanReceive(sessionUser, containerList, createTime);
            LOG.info("---saveScanReceiveByThread  异步执行 end");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            LOG.info("异常信息：" + e.toString());
//            txManager.rollback(status); // 回滚事务
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    @Async
    public void insertCirculateHistoryAndLatestByThread(List<Circulate> circulateOne, List<Circulate> circulateTwo,String deleteCirculateLatestSql)
    {
        LOG.info("---insertCirculateHistoryAndLatestByThread  异步执行 ");
        try {
            circulateService.insertCirculateHistoryAndLatest(circulateOne,circulateTwo,deleteCirculateLatestSql);
            LOG.info("---insertCirculateHistoryAndLatestByThread  异步执行 end");
        }
        catch (Exception e)
        {
            LOG.info("异常信息：" + e.toString());
//            txManager.rollback(status); // 回滚事务
        }
    }
}
