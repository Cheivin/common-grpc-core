package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 *
 */
@Slf4j
public class WeightRoundLoadBalancerTest {
    public static void main(String[] args) {
        LoadBalance balance = new WeightRoundLoadBalance();
        for (int i = 1; i < 4; i++) {
            balance.addInstance(RemoteInstance.builder()
                    .id(UUID.randomUUID().toString())
                    .weight(i)
                    .build());
        }
        log.info("{}", balance.getAll());
        for (int i = 0; i < 15; i++) {
            log.info("choose:{}", balance.choose());
        }
    }
}
