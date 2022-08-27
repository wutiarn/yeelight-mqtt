package ru.wtrn.yeelightmqtt.client;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

class YeelightSocketWrapperTest {

    private static final Logger logger = LoggerFactory.getLogger(YeelightSocketWrapperTest.class);

    @Test
    void sendCommand() throws Exception{
        InetAddress address = InetAddress.getByName("100.64.0.70");
        YeelightCommand command = YeelightCommand.builder()
                .method("toggle")
                .build();
        YeelightSocketWrapper socket = new YeelightSocketWrapper(address);
        try (socket) {
            YeelightResponse response = socket.sendCommand(command);
            logger.info("Response: {}", response);
        }
    }
}
