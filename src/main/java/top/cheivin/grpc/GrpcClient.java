package top.cheivin.grpc;

import com.google.gson.reflect.TypeToken;
import top.cheivin.grpc.core.Discover;
import top.cheivin.grpc.core.GrpcRequest;
import top.cheivin.grpc.core.RemoteInstance;
import top.cheivin.grpc.exception.InstanceException;
import top.cheivin.grpc.exception.InvokeException;
import top.cheivin.grpc.handle.Caller;
import top.cheivin.grpc.handle.caller.DefaultCaller;

import java.util.Collection;

/**
 * gRPC客户端
 */
public class GrpcClient {
    private Discover discover;
    private Caller caller;
    private int retryCount;

    public GrpcClient(Discover discover) {
        this(discover, new DefaultCaller());
    }

    public GrpcClient(Discover discover, Caller caller) {
        this(discover, caller, 1);
    }

    public GrpcClient(Discover discover, Caller caller, int retryCount) {
        this.discover = discover;
        this.caller = caller;
        this.retryCount = retryCount;
    }

    public void start() throws Exception {
        discover.start();
        // 应用关闭时关闭监听
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        discover.close();
    }

    public final <T> T invoke(GrpcRequest request, Class<T> resClass) throws InvokeException {
        return invoke(request, resClass, retryCount);
    }

    public final <C extends Collection<T>, T> C invoke(GrpcRequest request, Class<? extends Collection> collectionClass, Class<T> elementClass) throws InvokeException {
        return invoke(request, collectionClass, elementClass, retryCount);
    }

    public final <C extends Collection<T>, T> C invoke(GrpcRequest request, Class<? extends Collection> collectionClass, Class<T> elementClass, int retryCount) throws InvokeException {
        try {
            RemoteInstance remoteInstance = discover.getInstance(request);
            return (C) caller.call(remoteInstance, request, TypeToken.getParameterized(collectionClass, elementClass));
        } catch (InvokeException e) {
            // 仅拦截InvokeException，用于重试
            // 最后一次条用失败则抛出异常
            if (retryCount <= 1) {
                throw e;
            }
        }
        return invoke(request, collectionClass, elementClass, --retryCount);
    }

    public final <T> T invoke(GrpcRequest request, Class<T> resClass, int retryCount) throws InstanceException, InvokeException {
        try {
            RemoteInstance remoteInstance = discover.getInstance(request);
            return caller.call(remoteInstance, request, TypeToken.get(resClass));
        } catch (InvokeException e) {
            // 仅拦截InvokeException，用于重试
            // 最后一次条用失败则抛出异常
            if (retryCount <= 1) {
                throw e;
            }
        }
        return invoke(request, resClass, --retryCount);
    }
}
