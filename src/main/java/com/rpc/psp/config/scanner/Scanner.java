package com.rpc.psp.config.scanner;

import java.io.IOException;
import java.util.List;

/**
 * Create by GuangxiaoLong on
 */
public interface Scanner<T> {
    List<T> registerList() throws IOException;
}
