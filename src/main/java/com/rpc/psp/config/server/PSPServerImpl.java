package com.rpc.psp.config.server;

import com.rpc.psp.config.ServerConfig;
import com.rpc.psp.config.scanner.Scanner;
import com.rpc.psp.config.scanner.SpringContextScanner;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PSPServerImpl implements PSPServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PSPServer.class);
    // 默认配置
    public static final ServerConfig DEFAULT_CONFIG = new ServerConfig("com.walhao.psp.service", 9300);
    // 扫描器
    private Scanner<BindableService> serviceScanner;
    // Server 服务器
    private final Server server;
    // 端口
    private int port;

    public PSPServerImpl() {
        this(DEFAULT_CONFIG, new SpringContextScanner(DEFAULT_CONFIG.getBasepackage()));
    }

    public PSPServerImpl(Scanner<BindableService> scanner) {
        this(DEFAULT_CONFIG, scanner);
    }

    public PSPServerImpl(int port, Scanner<BindableService> scanner) {
        this.port = port;
        // 扫描器
        this.serviceScanner = scanner;
        // Server 服务器
        server = builderServer(port);
    }

    public PSPServerImpl(ServerConfig config, Scanner<BindableService> scanner) {
        this(config.getPort(), scanner);
    }

    // 初始化Server
    private Server builderServer(int port) {
        ServerBuilder<?> builder = ServerBuilder.forPort(port);
        try {
            serviceScanner.registerList().forEach((bindService) -> {
                builder.addService(bindService);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }

    //
    @Override
    public void start() {
        try {
            LOGGER.info("server started, listening on " + port);
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.err.println("shutting down PSP server since JVM is shutting down");
                    PSPServerImpl.this.stop();
                    System.err.println("server shut down");
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
        }
    }

    @Override
    public void blockUntilShutdown() {
        if (server != null) {
            try {
                LOGGER.info("Server Already started , begin blocking .");
                server.awaitTermination();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
