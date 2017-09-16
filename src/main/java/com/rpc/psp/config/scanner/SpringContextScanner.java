package com.rpc.psp.config.scanner;

import com.rpc.psp.config.register.Register;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by guangxiaoLong on 2017-09-15
 * Spring 集成扫描
 */
public class SpringContextScanner implements Scanner<Register>, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContextScanner.class);

    private static ApplicationContext context;

    private String basePackage;
    private ClassLoader classLoader;


    public SpringContextScanner(String basePackage) {
        this.basePackage = basePackage;
        this.classLoader = getClass().getClassLoader();
    }

    public SpringContextScanner(String basePackage, ClassLoader classLoader) {
        this.basePackage = basePackage;
        this.classLoader = classLoader;
    }

    private List<Class<Register>> doScan(String basePackage) throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);
        URL url = classLoader.getResource(splashPath);
        String filePath = StringUtil.getRootPath(url);
        // directory
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Scanner directory {}", filePath);
        }
        return readFromDirectory(filePath, basePackage);
    }

    private List<Class<Register>> readFromDirectory(String path, String packageName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Scanner path : {} , packageName : {}", path, packageName);
        }
        List<Class<Register>> returnList = new ArrayList<>();
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                List<Class<Register>> classes = readFromDirectory(file.getAbsolutePath(), packageName + '.' + file.getName());
                if (classes != null && classes.size() > 0)
                    returnList.addAll(classes);
                continue;
            }
            try {
                String name = file.getName();
                Class baseClass = Class.forName(packageName + '.' + name.substring(0, name.length() - 6));
                if (Register.class.isAssignableFrom(baseClass)) {
                    returnList.add(baseClass);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("{} is scanned", baseClass);
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return returnList.size() == 0 ? null : returnList;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public List<Register> registerList() throws IOException {
        List<Class<Register>> classes = doScan(basePackage);
        if (classes == null) return null;
        List<Register> registerList = new ArrayList<>();
        if (context == null) {
            LOGGER.error("context is error");
            throw new RuntimeException("spring init error ");
        }
        classes.forEach((registerClass -> {
            registerList.add(context.getBean(registerClass));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("load spring bean : [ {} ]", registerClass.getName());
            }
        }));
        LOGGER.info("load spring bean count is {}", registerList.size());
        return registerList;
    }
}
