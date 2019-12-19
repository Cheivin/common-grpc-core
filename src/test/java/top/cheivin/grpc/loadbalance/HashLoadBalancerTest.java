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
@DisplayName("hash负载均衡")
public class HashLoadBalancerTest {
    private LoadBalance balance = LoadBalanceFactory.getBalance(2);

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
        RemoteInstance instance = null;
        for (int i = 0; i <= 10; i++) {
            RemoteInstance choose = balance.choose();
            Assertions.assertNotNull(choose);
            log.info("choose:{}", choose);
            if (instance == null) {
                instance = choose;
            } else {
                Assertions.assertEquals(instance, choose, "instance not equals between twice choose");
            }
        }
    }

    @Test
    public void testChooseAfterAdd() {
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
    public void testChooseAfterRemove() {
        RemoteInstance choose = balance.choose();
        log.info("last choose,{}", choose);

        balance.removeInstance("1");

        log.info("choose:{}", balance.choose());
    }
}
