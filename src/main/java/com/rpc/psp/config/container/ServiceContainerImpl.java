package com.rpc.psp.config.container;

import io.grpc.BindableService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Create by guangxiaoLong on 2017-09-14
 * 单例容器
 */
public class ServiceContainerImpl implements ServiceContainer<BindableService> {
    private static final ArrayList<BindableService> container = new ArrayList<>();
    private Logger logger = Logger.getLogger(ServiceContainer.class);
    public static ServiceContainer serviceContainer = builder();

    private ServiceContainerImpl() {
        logger.info("Service Container is initialize.");
    }

    private static ServiceContainer builder() {
        if (serviceContainer != null) {
            return serviceContainer;
        }
        return new ServiceContainerImpl();
    }

    @Override
    public Iterator<BindableService> iterator() {
        return container.iterator();
    }

    @Override
    public void registerToContainer(BindableService service) {
        if (service == null)
            return;
        container.add(service);
    }
}
