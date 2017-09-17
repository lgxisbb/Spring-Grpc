package com.rpc.psp.config.scanner;

import io.grpc.BindableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rpc.psp.config.scanner.CollectClass.load;

/**
 * Create by guangxiaoLong on 2017-09-15
 * Spring 集成类
 */
public class SpringContextScanner implements Scanner<BindableService>, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContextScanner.class);
    private static ApplicationContext context;

    private final String basePackage;

    public SpringContextScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public List<BindableService> registerList() throws IOException {
        if (context == null) {
            LOGGER.error("context is error");
            throw new RuntimeException("spring init error ");
        }

        String splashPath = StringUtil.dotToSplash(basePackage);
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        String filePath = cl.getResource(splashPath).getFile();
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Scanner directory {}", filePath);
        List<Class<BindableService>> load = load(basePackage, new File(filePath), BindableService.class);
        if (load == null && 0 > load.size())
            return null;
        List<BindableService> bindableServices = new ArrayList<>();
        load.forEach((bindClass) -> {
            BindableService bean = context.getBean(bindClass);
            if (bean != null) bindableServices.add(bean);
            else LOGGER.info(" class : {} not found", bindClass.getName());
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("load spring bean : [ {} ]", bindClass.getName());
        });
        LOGGER.info("load spring bean count is {}", bindableServices.size());
        return bindableServices;
    }
}
