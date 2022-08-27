package ru.wtrn.yeelightmqtt.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

class YeelightCeilingLightDeviceTest {

    private static final Logger logger = LoggerFactory.getLogger(YeelightCeilingLightDeviceTest.class);

    @Test
    void sendCommand() throws Exception{
        InetAddress address = InetAddress.getByName("100.64.0.70");
        YeelightCeilingLightDevice device = new YeelightCeilingLightDevice(address, "test-lamp");
        JsonNode response = device.toggle();
        String resultCode = response.get("result").get(0).asText();
        Assertions.assertThat(resultCode).isEqualTo("ok");
    }
}
