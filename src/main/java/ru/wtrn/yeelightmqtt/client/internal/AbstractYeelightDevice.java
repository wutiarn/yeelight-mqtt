package ru.wtrn.yeelightmqtt.client.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.wtrn.yeelightmqtt.client.YeelightCommand;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractYeelightDevice {
    private final InetAddress targetAddress;
    private final String deviceName;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger requestIdCounter = new AtomicInteger();

    private final DeviceEventsCollector deviceEventsCollector = new DeviceEventsCollector();
    private volatile Socket socket = null;

    public AbstractYeelightDevice(InetAddress targetAddress, String deviceName) {
        this.targetAddress = targetAddress;
        this.deviceName = deviceName;
        startEventsListener();
    }

    @SneakyThrows
    protected synchronized int sendCommand(YeelightCommand command) {
        YeelightCommandWithId request = new YeelightCommandWithId(command, requestIdCounter.incrementAndGet());
        String requestString = objectMapper.writeValueAsString(request);
        byte[] bytes = (requestString + "\r\n").getBytes();
        OutputStream outputStream = getOrCreateSocket().getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        return request.id;
    }

    private synchronized Socket getOrCreateSocket() throws Exception {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(targetAddress, 55443);
            socket.setKeepAlive(true);
            socket.setSoTimeout(60_000);
        }
        return socket;
    }

    private void startEventsListener() {
        new DeviceEventsListener(new DeviceEventsListener.DeviceFacade() {
            @Override
            public String getName() {
                return deviceName;
            }

            @Override
            public InputStream tryGetInputStream() throws Exception {
                return getOrCreateSocket().getInputStream();
            }

            @Override
            public void onNextEvent(String event) {
                deviceEventsCollector.onNewEvent(event);
            }
        }).start();
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
