package ru.wtrn.yeelightmqtt.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class YeelightResponse {
    private final String method;
    private final List<Object> params;
}
