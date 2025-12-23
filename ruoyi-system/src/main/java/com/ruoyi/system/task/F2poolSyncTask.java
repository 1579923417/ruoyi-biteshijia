package com.ruoyi.system.task;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import com.ruoyi.system.service.F2PoolService;
/**
 * F2Pool 矿机数据定时同步任务
 *
 * <p>
 * 该任务用于定时从 F2Pool 拉取矿机的最新数据，
 * 并同步更新本地数据库中的矿机状态与收益信息。
 * </p>
 *
 * @author Jamie
 */
@Component("f2poolSyncTask")
public class F2poolSyncTask {
    @Autowired
    private F2PoolService f2PoolService;

    /**
     * 定时执行矿机数据同步
     *
     * <p>
     * 调用 {@link F2PoolService#syncMinerData()} 方法，
     * 统一完成所有用户下矿机的数据拉取与本地更新。
     * </p>
     */
    public void sync() {
        f2PoolService.syncMinerData();
    }
}

