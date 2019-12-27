package top.cheivin.grpc.util;

/**
 * 全局ID生成器，基于时间 +ip+ms级别的自增
 */
public class IdWorker {
    private static final long twepoch = 1576771200000L;
    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 8L;
    //
    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    private long ip;

    private IdWorker() {
        // 这个方法有风险 ，1.ip取不到，ip碰撞了
        this.ip = NetWorkAddressUtils.localIpLong();
        this.ip = this.ip % 64; //2^6 = 63 只支持64台机器，如果超过了，改方案，全局id生成器
    }

    public static void setIp(long ip) {
        IdWorkerHelper.idWorker.ip = ip;
    }


    private long[] splitId(long id) {
        long[] ls = new long[3];
        ls[0] = id >> 14;
        ls[1] = (id & 0x3fff) >> 8;
        ls[2] = id & 0xff;
        return ls;
    }

    /**
     * 获取时间
     *
     * @param id id序号
     * @return
     */
    public static long getTime(long id) {
        return IdWorkerHelper.idWorker.splitId(id)[0] + twepoch;
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    private synchronized long nextId() {
        long timestamp = timeGen();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("系统时钟混乱了  %d", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << 14) //
                | (ip << 8)
                | sequence;
    }

    public static long next() {
        return IdWorkerHelper.idWorker.nextId();
    }

    public static long cusId(long time) {
        return cusId(time, 0L, 0L);
    }

    /**
     * 自定义生成id
     *
     * @param time 毫秒
     * @param ip
     * @param sn
     * @return
     */
    public static long cusId(long time, long ip, long sn) {
        return ((time - twepoch) << 14)
                | (ip << 8)
                | sn;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    private static class IdWorkerHelper {
        static IdWorker idWorker = new IdWorker();
    }
}
