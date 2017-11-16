package com.rpc.psp.config.scanner;

import io.grpc.BindableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rpc.psp.config.scanner.CollectClass.load;

/**
 * 扫描包
 * Create by guangxiaoLong on 2017-09-14
 */
public class ClasspathScanner implements Scanner<BindableService> {
    private Logger LOGGER = LoggerFactory.getLogger(ClasspathScanner.class);

    private String basePackage;

    public ClasspathScanner(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public List<BindableService> registerList() throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        String filePath = cl.getResource(splashPath).getFile();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Scanner directory {}", filePath);
        }
        List<Class<BindableService>> load = load(basePackage, new File(filePath), BindableService.class);
        if (load == null || 0 == load.size())
            return null;
        List<BindableService> bindableServices = new ArrayList<>();
        load.forEach((bindClass) -> {
            try {
                bindableServices.add(bindClass.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return bindableServices;
    }
}

