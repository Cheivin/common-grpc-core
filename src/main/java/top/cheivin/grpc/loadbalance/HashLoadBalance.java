package top.cheivin.grpc.loadbalance;

import top.cheivin.grpc.core.RemoteInstance;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * hash均衡
 */
public class HashLoadBalance extends LoadBalance {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private String instanceId;

    @Override
    void add(RemoteInstance instance) {
        resetId();
    }

    @Override
    void remove(RemoteInstance instance) {
        resetId();
    }

    /**
     * 每次变化的时候将id置空
     */
    private void resetId() {
        this.lock.writeLock().lock();
        try {
            this.instanceId = null;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public RemoteInstance choose() {
        String id;
        this.lock.readLock().lock();
        try {
            if (this.instanceId != null) {
                id = this.instanceId;
            } else {
                // id未缓存时，重新hash选择id
                RemoteInstance instance = this.get(this.hashCode() % this.size());
                this.instanceId = instance.getId();
                return instance;
            }
        } finally {
            this.lock.readLock().unlock();
        }
        if (id.isEmpty()) {
            return null;
        }
        return this.get(id);
    }
}
