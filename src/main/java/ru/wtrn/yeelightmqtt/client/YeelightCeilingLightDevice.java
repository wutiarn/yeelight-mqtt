package ru.wtrn.yeelightmqtt.client;

import ru.wtrn.yeelightmqtt.client.internal.AbstractYeelightDevice;

import java.net.InetAddress;

public class YeelightCeilingLightDevice extends AbstractYeelightDevice {
    public YeelightCeilingLightDevice(InetAddress targetAddress, String deviceName) {
        super(targetAddress, deviceName);
    }

    public void toggle() {
        YeelightCommand command = YeelightCommand.builder()
                .method("toggle")
                .build();
        sendCommand(command);
    }
}
