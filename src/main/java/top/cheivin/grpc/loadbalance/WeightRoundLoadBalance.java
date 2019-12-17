package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 平滑加权轮询负载均衡
 */
public class WeightRoundLoadBalance extends AbstractLoadBalance {
    private ConcurrentHashMap<String, Integer[]> curWeightMap = new ConcurrentHashMap<>();
    private AtomicInteger weightSum = new AtomicInteger();

    @Override
    void add(RemoteInstance instance) {
        this.weightSum.getAndAdd(instance.getWeight());
        this.curWeightMap.put(instance.getId(), new Integer[]{instance.getWeight(), instance.getWeight()});
    }

    @Override
    void remove(RemoteInstance instance) {
        this.weightSum.getAndAdd(-instance.getWeight());
        this.curWeightMap.remove(instance.getId());
    }

    @Override
    public RemoteInstance choose() {
        List<Map.Entry<String, Integer[]>> entryList = new ArrayList<>(this.curWeightMap.entrySet());
        // 选取权重最大的instanceId
        Optional<Map.Entry<String, Integer[]>> entryOpt = entryList.stream().max((a, b) -> {
            if (a.getValue()[0] > b.getValue()[0]) {
                return 1;
            } else if (a.getValue()[0].equals(b.getValue()[0])) {
                return 0;
            }
            return -1;
        });
        entryList.clear();
        if (!entryOpt.isPresent()) {
            return null;
        }
        Map.Entry<String, Integer[]> entry = entryOpt.get();
        Integer[] weights = entry.getValue();
        weights[0] = weights[0] - this.weightSum.get();
        // 更新当前权重
        this.curWeightMap.put(entry.getKey(), weights);
        // 加权所有权重
        this.curWeightMap.values().forEach(v -> {
            v[0] = v[0] + v[1];
        });
        // 返回实例
        return this.get(entry.getKey());
    }
}
