package com.rpc.psp.config.register;

import com.rpc.psp.config.container.ServiceContainer;
import com.rpc.psp.config.container.ServiceContainerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Create by guangxiaoLong on 2017-09-14
 * PSP服务注册器
 */
public class PHPServierRegister {
    private static final Logger logger = LoggerFactory.getLogger(PHPServierRegister.class);
    private static final ServiceContainer container = ServiceContainerImpl.serviceContainer;

    private List<? extends Register> registeres;

    public PHPServierRegister(List<? extends Register> registeres) {
        this.registeres = registeres;
        beginRegister();
    }

    private void beginRegister() {
        registeres.forEach((service) -> {
            service.register(container);
        });
    }
}
