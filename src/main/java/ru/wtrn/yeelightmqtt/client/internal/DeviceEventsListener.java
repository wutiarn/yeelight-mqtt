package ru.wtrn.yeelightmqtt.client.internal;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class DeviceEventsListener {
    private final DeviceFacade deviceFacade;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private boolean connected = false;

    private static final Logger logger = LoggerFactory.getLogger(DeviceEventsListener.class);

    DeviceEventsListener(DeviceFacade deviceFacade) {
        this.deviceFacade = deviceFacade;
    }

    public void start() {
        Thread thread = new Thread(this::run, deviceFacade.getName() + "-listener");
        thread.setDaemon(true);
        thread.start();
    }

    private void loop() throws Exception {
        InputStream inputStream = deviceFacade.tryGetInputStream();
        Scanner scanner = new Scanner(inputStream);

        logger.info("Event listener for device {} started", deviceFacade.getName());

        //noinspection InfiniteLoopStatement
        while (true) {
            String line = scanner.nextLine();
            logger.info("New event from device {}: {}", deviceFacade.getName(), line);
            deviceFacade.onNextEvent(line);
        }
    }

    @SneakyThrows
    private void run() {
        do {
            try {
                loop();
            } catch (Exception e) {
                if (connected) {
                    logger.info("Event listener for device {} failed", deviceFacade.getName(), e);
                    connected = false;
                }
                // pass
            }
        } while (!shutdownLatch.await(30, TimeUnit.SECONDS));
    }

    interface DeviceFacade {
        String getName();
        InputStream tryGetInputStream() throws Exception;
        void onNextEvent(String event);
    }
}
