package ru.wtrn.yeelightmqtt.client;

import com.fasterxml.jackson.databind.JsonNode;
import ru.wtrn.yeelightmqtt.client.internal.AbstractYeelightDevice;

import java.net.InetAddress;

public class YeelightCeilingLightDevice extends AbstractYeelightDevice {
    public YeelightCeilingLightDevice(InetAddress targetAddress, String deviceName) {
        super(targetAddress, deviceName);
    }

    public JsonNode toggle() {
        YeelightCommand command = YeelightCommand.builder()
                .method("toggle")
                .build();
        return sendCommand(command);
    }
}
