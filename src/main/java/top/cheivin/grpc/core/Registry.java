package top.cheivin.grpc.core;

import java.util.Collection;

public interface Registry {

    void start(int serverPort) throws Exception;

    void close();

    void addService(Collection<ServiceInfo> infos);

}
