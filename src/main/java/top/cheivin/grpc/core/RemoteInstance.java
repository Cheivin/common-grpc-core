package top.cheivin.grpc.core;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 远程服务提供者实例
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoteInstance {
    /**
     * 实例id
     */
    private String id;
    /**
     * 实例地址
     */
    private String ip;
    /**
     * 实例端口
     */
    private int port;
    /**
     * 权重
     */
    private int weight;
    /**
     * gRPC连接
     */
    private ManagedChannel channel;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return id.equals(((RemoteInstance) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void close() {
        if (!isClosed()) {
            this.channel.shutdown();
        }
    }

    public boolean isClosed() {
        return channel == null || channel.isShutdown();
    }

    public void connect() {
        if (isClosed()) {
            this.channel = ManagedChannelBuilder.forAddress(ip, port).usePlaintext(true).build();
        }
    }

    @Override
    public String toString() {
        return "RemoteInstance{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                '}';
    }
}
