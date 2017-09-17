package com.rpc.psp.config.scanner;


import io.grpc.BindableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuangxiaoLong on 2017-09-16
 */
public class CollectClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectClass.class);

    public static <T> List<Class<T>> load(String basepackage, File baseFile, Class<T> filterClass) {
        if (LOGGER.isDebugEnabled())
            LOGGER.info("Scanner path : {} , packageName : {}", baseFile.getAbsolutePath(), basepackage);
        List<Class<T>> returnList = new ArrayList<>();
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                List<Class<T>> classes = load(basepackage + '.' + file.getName(), file, filterClass);
                if (classes != null && classes.size() > 0)
                    returnList.addAll(classes);
                continue;
            }
            try {
                String name = file.getName();
                Class baseClass = Class.forName(basepackage + '.' + name.substring(0, name.length() - 6));
                if (filterClass != null && filterClass.isAssignableFrom(baseClass))
                    returnList.add(baseClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return returnList.size() == 0 ? null : returnList;
    }

    public static void main(String[] args) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        String basepackage = "com.rpc.psp.config";
        File file = new File(cl.getResource(StringUtil.dotToSplash(basepackage)).getFile());
        List<Class<Scanner>> load = load(basepackage, file, Scanner.class);
        load.forEach((aClass -> {
            System.out.println(aClass.getName());
        }));
    }
}
