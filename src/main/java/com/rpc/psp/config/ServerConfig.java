package com.rpc.psp.config;

public class ServerConfig {

    private String basepackage;
    private int port;

    public ServerConfig() {
    }

    public ServerConfig(String basepackage, int port) {
        this.basepackage = basepackage;
        this.port = port;
    }

    public String getBasepackage() {
        return basepackage;
    }

    public void setBasepackage(String basepackage) {
        this.basepackage = basepackage;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
