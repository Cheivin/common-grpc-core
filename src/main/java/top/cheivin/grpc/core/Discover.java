package top.cheivin.grpc.core;


import top.cheivin.grpc.exception.ChannelException;
import top.cheivin.grpc.exception.InstanceException;

public interface Discover {
    void start() throws Exception;

    void close();

    RemoteInstance getInstance(GrpcRequest request) throws InstanceException, ChannelException;
}
