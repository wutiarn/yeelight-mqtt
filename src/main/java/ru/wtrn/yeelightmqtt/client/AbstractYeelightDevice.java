package ru.wtrn.yeelightmqtt.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AbstractYeelightDevice {
    private final InetAddress targetAddress;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger requestIdCounter = new AtomicInteger();
    private volatile Socket socket = null;

    public AbstractYeelightDevice(InetAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    @SneakyThrows
    protected int sendCommand(YeelightCommand command) {
        YeelightCommandWithId request = new YeelightCommandWithId(command, requestIdCounter.incrementAndGet());
        String requestString = objectMapper.writeValueAsString(request);
        byte[] bytes = (requestString + "\r\n").getBytes();
        withSocket((socket) -> {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        });
        return request.id;
    }

    private synchronized void withSocket(SocketAction action) throws Exception {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(targetAddress, 55443);
            socket.setKeepAlive(true);
            socket.setSoTimeout(60_000);
        }
        action.apply(socket);
    }

    @Getter
    private static class YeelightCommandWithId extends YeelightCommand {
        private final int id;

        YeelightCommandWithId(YeelightCommand command, int id) {
            super(command.getMethod(), command.getParams());
            this.id = id;
        }
    }

    @FunctionalInterface
    private interface SocketAction {
        void apply(Socket socket) throws Exception;
    }
}
