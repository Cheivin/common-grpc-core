package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 *
 */
@Slf4j
public class HashLoadBalancerTest {
    public static void main(String[] args) {
        LoadBalance balance = new HashLoadBalance();
        for (int i = 0; i < 3; i++) {
            balance.addInstance(RemoteInstance.builder()
                    .id(UUID.randomUUID().toString())
                    .build());
        }
        log.info("{}", balance.getAll());
        for (int i = 0; i < 3; i++) {
            log.info("choose:{}",balance.choose());
        }

        log.info("add instance");
        balance.addInstance(RemoteInstance.builder()
                .id(UUID.randomUUID().toString())
                .build());

        for (int i = 0; i < 3; i++) {
            log.info("choose:{}",balance.choose());
        }
    }
}
