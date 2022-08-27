package ru.wtrn.yeelightmqtt.client.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DeviceEventsCollector {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DeviceEventsCollector.class);

    private final ConcurrentMap<Object, Object> eventsCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()
            .asMap();

    public DeviceEventsCollector() {

    }

    public void onNewEvent(String event) {
        JsonNode eventNode;
        try {
            eventNode = objectMapper.readTree(event);
        } catch (JsonProcessingException e) {
            logger.error("Failed to read event to JsonNode: {}", event);
            return;
        }
        JsonNode eventId = eventNode.get("id");
        if (eventId != null && eventId.isNumber()) {
            eventsCache.put(eventId.intValue(), eventNode);
        }
    }
}
