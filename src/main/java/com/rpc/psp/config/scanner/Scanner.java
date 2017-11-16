package com.rpc.psp.config.scanner;

import java.util.List;

/**
 * Create by GuangxiaoLong on 2017-09-10
 * 这个接口返回一个集合,集合中的元素会被注册到PSPServer中
 */
public interface Scanner<T> {
    List<T> registerList() throws Exception;
}
