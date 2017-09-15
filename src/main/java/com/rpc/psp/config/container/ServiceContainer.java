package com.rpc.psp.config.container;

/**
 * 容器接口
 * Create by guangxiaoLong on 2017-09-14
 *
 * @param <T>
 */
public interface ServiceContainer<T> extends Iterable<T> {
    void registerToContainer(T t);
}
