package com.rpc.psp.config.scanner;

import com.rpc.psp.config.register.Register;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描包
 * Create by guangxiaoLong on 2017-09-14
 */
public class ClasspathScanner implements Scanner<Register> {
    private Logger logger = LoggerFactory.getLogger(ClasspathScanner.class);

    private String basePackage;
    private ClassLoader cl;

    public ClasspathScanner(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();

    }

    @Override
    public List<Register> registerList() throws IOException {
        logger.info("Begin Scanner {}", basePackage);
        return doScan(basePackage);
    }

    /**
     * @param basePackage
     * @return
     * @throws IOException
     */
    private List<Register> doScan(String basePackage) throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);
        URL url = cl.getResource(splashPath);
        String filePath = StringUtil.getRootPath(url);
        // directory
        if (logger.isDebugEnabled()) {
            logger.debug("Scanner directory {}", filePath);
        }
        return readFromDirectory(filePath);
    }

    private List<Register> readFromDirectory(String path) {
        List<Register> returnList = new ArrayList<>();
        File[] files = new File(path).listFiles();
        for (File file : files) {
            if (file.isDirectory())
                continue;
            try {
                String name = file.getName();
                Class baseClass = Class.forName(basePackage + '.' + name.substring(0, name.length() - 6));
                if (Register.class.isAssignableFrom(baseClass)) {
                    try {
                        Register register = (Register) baseClass.newInstance();
                        returnList.add(register);
                        if (logger.isDebugEnabled()) {
                            logger.debug("{} is scanned", baseClass);
                        }
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return returnList.size() == 0 ? null : returnList;
    }

    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

}

