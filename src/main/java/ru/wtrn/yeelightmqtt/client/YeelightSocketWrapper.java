package ru.wtrn.yeelightmqtt.client;

import java.net.InetAddress;
import java.util.concurrent.locks.ReentrantLock;

public class YeelightSocketWrapper {
    private final InetAddress targetAddress;
    private final ReentrantLock lock = new ReentrantLock(true);

    public YeelightSocketWrapper(InetAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    public YeelightResponse sendCommand(YeelightCommand command) {
        return null;
    }
}
