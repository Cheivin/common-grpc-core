package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡
 */
public class RoundLoadBalance extends LoadBalance {
    private static final int MAX_VAL = 1000000;
    private AtomicInteger inc = new AtomicInteger();

    @Override
    void add(RemoteInstance instance) {
        // ignore
    }

    @Override
    void remove(RemoteInstance instance) {
        // ignore
    }

    @Override
    public RemoteInstance choose() {
        int size = this.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return this.get(0);
        }
        int count = this.inc.getAndIncrement();
        if (count > MAX_VAL) {
            this.inc.set(0);
        }
        return this.get(count % size);
    }
}
