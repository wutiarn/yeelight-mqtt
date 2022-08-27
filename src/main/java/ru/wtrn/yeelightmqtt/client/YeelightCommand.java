package ru.wtrn.yeelightmqtt.client;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class YeelightCommand {
    private final String method;
    private final List<Object> params;
}
