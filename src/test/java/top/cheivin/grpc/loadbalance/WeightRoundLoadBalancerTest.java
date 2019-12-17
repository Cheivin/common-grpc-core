package top.cheivin.grpc.loadbalance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import top.cheivin.grpc.core.RemoteInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 *
 */
@Slf4j
public class WeightRoundLoadBalancerTest {
    private LoadBalance balance = LoadBalanceFactory.getBalance(3);

    @Before
    public void init() {
        for (int i = 1; i < 4; i++) {
            balance.addInstance(RemoteInstance.builder()
                    .id(String.valueOf(i))
                    .weight(i)
                    .build());
        }
        log.info("loadBalance:{}", balance);
    }

    @Test
    public void testChoose() {
        for (int i = 0; i < 10; i++) {
            RemoteInstance choose = balance.choose();
            Assert.assertNotNull(choose);
            log.info("choose:{}", choose);
        }
    }

    @Test
    public void testAddAndChoose() {
        RemoteInstance choose = balance.choose();
        log.info("last choose,{}", choose);

        RemoteInstance instance = RemoteInstance.builder()
                .id(String.valueOf(999))
                .build();
        log.info("add instance,{}", instance);
        balance.addInstance(instance);

        log.info("choose:{}", balance.choose());
    }

    @Test
    public void testChooseAfterAdd() {
        RemoteInstance choose = balance.choose();
        log.info("last choose,{}", choose);

        balance.removeInstance("1");

        log.info("choose:{}", balance.choose());
    }
}
