package com.rpc.psp.config.register;

import com.rpc.psp.config.container.ServiceContainer;
import io.grpc.BindableService;

/**
 * PSP服务注册器
 */
public interface Register {

    void register(ServiceContainer<io.grpc.BindableService> container);

}
