package ru.wtrn.yeelightmqtt.client;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

class YeelightCeilingLightDeviceTest {

    private static final Logger logger = LoggerFactory.getLogger(YeelightCeilingLightDeviceTest.class);

    @Test
    void sendCommand() throws Exception{
        InetAddress address = InetAddress.getByName("100.64.0.70");
        YeelightCeilingLightDevice device = new YeelightCeilingLightDevice(address);
        device.toggle();
    }
}
