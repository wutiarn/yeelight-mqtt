package ru.wtrn.yeelightmqtt.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class YeelightSocketWrapper implements Closeable {
    private final InetAddress targetAddress;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger requestIdCounter = new AtomicInteger();
    private volatile Socket socket = null;

    public YeelightSocketWrapper(InetAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    @SneakyThrows
    public YeelightResponse sendCommand(YeelightCommand command) {
        YeelightCommandWithId request = new YeelightCommandWithId(command, requestIdCounter.incrementAndGet());
        byte[] bytes = objectMapper.writeValueAsBytes(request);
        withSocket((socket) -> {
            socket.getOutputStream().write(bytes);
            Scanner scanner = new Scanner(socket.getInputStream());
            String responseStr = scanner.nextLine();
            return objectMapper.readValue(responseStr, YeelightResponse.class);
        });
        return null;
    }

    private synchronized YeelightResponse withSocket(SocketAction action) throws Exception {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(targetAddress, 55443);
            socket.setKeepAlive(true);
            socket.setSoTimeout(60_000);
        }
        return action.apply(socket);
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
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
        YeelightResponse apply(Socket socket) throws Exception;
    }
}
