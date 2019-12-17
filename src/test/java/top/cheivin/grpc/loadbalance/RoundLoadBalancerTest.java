package top.cheivin.grpc.loadbalance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import top.cheivin.grpc.core.RemoteInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 *
 */
@Slf4j
public class RoundLoadBalancerTest {
    private LoadBalance balance = LoadBalanceFactory.getBalance(0);

    @Before
    public void init() {
        for (int i = 0; i < 10; i++) {
            balance.addInstance(RemoteInstance.builder()
                    .id(String.valueOf(i))
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
            Assert.assertEquals("not round", String.valueOf(i), choose.getId());
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
