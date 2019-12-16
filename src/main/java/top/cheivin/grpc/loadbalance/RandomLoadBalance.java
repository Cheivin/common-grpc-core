package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;

import java.util.Random;

/**
 * 随机均衡
 */
public class RandomLoadBalance extends LoadBalance {
    private final Random r = new Random(System.currentTimeMillis());

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
        return this.get(r.nextInt(size));
    }
}
