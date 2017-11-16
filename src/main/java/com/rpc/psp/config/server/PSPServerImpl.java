package com.rpc.psp.config.server;

import com.rpc.psp.config.scanner.Scanner;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * @Auther GuangxiaoLong
 * 这个类装饰了Grpc的Server类
 * 通过对外提供start()和stop()方法来确定行为
 * <p>
 * 这个类的构造器只有一种,
 * PSPServerImpl(int port, Scanner<BindableService> scanner)
 * 参数1 : 服务的端口号
 * 参数2 : 实现 {@link com.rpc.psp.config.scanner.Scanner}接口
 */
public class PSPServerImpl implements PSPServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PSPServer.class);
    private final Server server;

    public PSPServerImpl(int port, Scanner<BindableService> scanner) {
        server = builderServer(port, scanner);
    }

    // 初始化Server
    private Server builderServer(int port, Scanner<BindableService> scanner) {
        ServerBuilder<?> builder = ServerBuilder.forPort(port);
        try {
            scanner.registerList().forEach((bindService) ->
                    builder.addService(bindService)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }

    @Override
    public void start() {
        try {
            server.start();
            LOGGER.info("server started, listening on " + server.getPort());
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    LOGGER.info("shutting down PSP server since JVM is shutting down");
                    PSPServerImpl.this.stop();
                    LOGGER.info("server shut down");
                }
            });
        } catch (IOException e) {
            LOGGER.trace("server start is error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
            LOGGER.info("PSP Grpc-server has stopped.");
        }
    }
}
