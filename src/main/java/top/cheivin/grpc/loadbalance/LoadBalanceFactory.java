package top.cheivin.grpc.loadbalance;

/**
 *
 */
public class LoadBalanceFactory {
    public static LoadBalance getBalance(int type) {
        switch (type) {
            case 0:// 轮询
                return new RoundLoadBalance();
            case 1:// 随机
                return new RandomLoadBalance();
            case 2:// hash
                return new HashLoadBalance();
            case 3:// 平滑权重轮询
                return new WeightRoundLoadBalance();
            default:
                return new RoundLoadBalance();
        }
    }
}
