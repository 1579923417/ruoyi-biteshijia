package com.ruoyi.web.task;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import com.ruoyi.system.service.F2PoolService;

@Component
public class F2poolSyncTask {
    @Autowired
    private F2PoolService f2PoolService;

    @Scheduled(cron = "0 0 * * * ?")
    public void sync() {
        f2PoolService.syncMinerData();
    }
}

