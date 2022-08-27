package ru.wtrn.yeelightmqtt.client;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@Builder
@ToString
public class YeelightCommand {
    private final String method;
    @Builder.Default
    private final List<Object> params = List.of();
}
