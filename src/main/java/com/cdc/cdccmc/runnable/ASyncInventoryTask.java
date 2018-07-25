package com.cdc.cdccmc.runnable;

import com.cdc.cdccmc.domain.InventoryDetail;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.InventoryDetailService;
import com.cdc.cdccmc.service.InventoryMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ASyncInventoryTask {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ASyncInventoryTask.class);
    @Autowired
    private InventoryMainService inventoryMainService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void addInventoryDetailByCirculateLatestByThread(SystemUser sessionUser, InventoryMain inventoryMain
            , String orgId, String ciculateStateCode, Integer start, Integer limit)
    {
        LOG.info("---addInventoryDetailByCirculateLatestByThread  异步执行  Thread="+Thread.currentThread().getName());
        try {
            inventoryMainService.addInventoryDetailByCirculateLatest(sessionUser,inventoryMain,orgId,ciculateStateCode,start,limit);
            LOG.info("---addInventoryDetailByCirculateLatestByThread  异步执行 end  Thread="+Thread.currentThread().getName());
        }
        catch (Exception e)
        {
            LOG.info("Thread="+Thread.currentThread().getName()+"  异常信息：" + e.toString());
//            txManager.rollback(status); // 回滚事务
        }
    }

}
