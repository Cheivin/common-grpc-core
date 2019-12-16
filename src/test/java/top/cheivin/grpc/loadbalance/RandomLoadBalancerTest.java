package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 *
 */
@Slf4j
public class RandomLoadBalancerTest {
    public static void main(String[] args) {
        LoadBalance balance = new RandomLoadBalance();
        for (int i = 0; i < 3; i++) {
            balance.addInstance(RemoteInstance.builder()
                    .id(UUID.randomUUID().toString())
                    .build());
        }
        log.info("{}", balance.getAll());
        for (int i = 0; i < 15; i++) {
            log.info("choose:{}",balance.choose());
        }

    }
}
