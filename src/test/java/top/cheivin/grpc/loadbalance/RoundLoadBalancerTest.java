package top.cheivin.grpc.loadbalance;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import top.cheivin.grpc.core.RemoteInstance;

/**
 *
 */
@Slf4j
@DisplayName("轮询负载均衡")
public class RoundLoadBalancerTest {
    private LoadBalance balance = LoadBalanceFactory.getBalance(0);

    @BeforeEach
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
            Assertions.assertNotNull(choose);
            log.info("choose:{}", choose);
            Assertions.assertEquals(String.valueOf(i), choose.getId(), "not round");
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
