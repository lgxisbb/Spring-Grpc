package com.rpc.psp.config.scanner;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by GuangxiaoLong on 2017-09-16
 */
public class CollectClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectClass.class);


    /**
     * 在${packagePath}目录下查找符合${filterClass}的class
     *
     * @param packagePath 路径字符串
     * @param baseFile    基础文件家
     * @param filterClass 过滤类
     * @return ${baseFile}\ 下边符合filterClass的类
     */
    public static <T> List<Class<T>> load(String packagePath, File baseFile, Class<T> filterClass) {
        if (LOGGER.isDebugEnabled())
            LOGGER.info("Scanner path : {} , packageName : {}", baseFile.getAbsolutePath(), packagePath);
        List<Class<T>> returnList = new ArrayList<>();
        File[] files = baseFile.listFiles();
        for (File file : files) {
            /*
                如果是文件夹
                递归扫描这个包下的内容
                返回结果就用当前这层的集合容器进行接纳
                没有返回结果
                直接跳出当前这层文件扫描
             */
            if (file.isDirectory()) {
                List<Class<T>> classes = load(packagePath + '.' + file.getName(), file, filterClass);
                if (classes != null && classes.size() > 0)
                    returnList.addAll(classes);
                continue;
            }
            /*
                如果是文件
                判读是不是需和filterClass匹配
                匹配则添加到集合容器
             */
            try {
                String name = file.getName();
                Class baseClass = Class.forName(packagePath + '.' + name.substring(0, name.length() - 6));
                if (filterClass != null && filterClass.isAssignableFrom(baseClass))
                    returnList.add(baseClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return returnList.size() == 0 ? null : returnList;
    }
}
