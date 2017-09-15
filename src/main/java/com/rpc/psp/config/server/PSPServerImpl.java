package com.rpc.psp.config.server;

import com.rpc.psp.config.container.ServiceContainer;
import com.rpc.psp.config.container.ServiceContainerImpl;
import com.rpc.psp.config.register.PHPServierRegister;
import com.rpc.psp.config.scanner.Scanner;
import com.rpc.psp.config.scanner.SpringContextScanner;
import com.rpc.psp.config.ServerConfig;
import com.rpc.psp.config.register.Register;
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
    // Service 容器
    private static final ServiceContainer<BindableService> CONTAINER = ServiceContainerImpl.serviceContainer;
    // Server 服务器
    private final Server server;
    // 端口
    private int port;

    public PSPServerImpl() {
        this(DEFAULT_CONFIG, new SpringContextScanner(DEFAULT_CONFIG.getBasepackage()));
    }

    public PSPServerImpl(ServerConfig config) {
        this(config, new SpringContextScanner(DEFAULT_CONFIG.getBasepackage()));
    }

    public PSPServerImpl(Scanner<Register> scanner) {
        this(DEFAULT_CONFIG, scanner);
    }

    public PSPServerImpl(int port, Scanner<Register> scanner) {
        this.port = port;
        // 扫描器
        scannerClass(scanner);
        // Server 服务器
        server = builderServer(port);
    }

    public PSPServerImpl(ServerConfig config, Scanner<Register> registerScanner) {
        this.port = config.getPort();
        // 扫描器
        scannerClass(registerScanner);
        // Server 服务器
        server = builderServer(config.getPort());
    }

    // 初始化Server

    private static Server builderServer(int port) {
        ServerBuilder<?> builder = ServerBuilder.forPort(port);
        CONTAINER.forEach((service) -> {
            builder.addService(service);
        });
        return builder.build();
    }

    // 开始扫描
    private static void scannerClass(Scanner<Register> scanner) {
        try {
            new PHPServierRegister(scanner.registerList());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
