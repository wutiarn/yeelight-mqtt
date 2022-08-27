package ru.wtrn.yeelightmqtt.client.device;

import com.fasterxml.jackson.databind.JsonNode;
import ru.wtrn.yeelightmqtt.client.dto.YeelightCommand;
import ru.wtrn.yeelightmqtt.client.internal.AbstractYeelightDevice;

import java.net.InetAddress;

public class GenericYeelightDevice extends AbstractYeelightDevice {
    public GenericYeelightDevice(InetAddress targetAddress, String deviceName) {
        super(targetAddress, deviceName);
    }

    public JsonNode toggle() {
        YeelightCommand command = YeelightCommand.builder()
                .method("toggle")
                .build();
        return sendCommand(command);
    }
}
