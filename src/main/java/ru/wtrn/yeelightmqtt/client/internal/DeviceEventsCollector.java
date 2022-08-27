package ru.wtrn.yeelightmqtt.client.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class DeviceEventsCollector {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DeviceEventsCollector.class);

    private final ConcurrentMap<Integer, JsonNode> recentEvents = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .<Integer, JsonNode>build()
            .asMap();

    private final ConcurrentMap<Integer, CompletableFuture<JsonNode>> eventAwaitingFutures = new ConcurrentHashMap<>();

    public void onNewEvent(String event) {
        JsonNode eventNode;
        try {
            eventNode = objectMapper.readTree(event);
        } catch (JsonProcessingException e) {
            logger.error("Failed to read event to JsonNode: {}", event);
            return;
        }
        JsonNode eventIdNode = eventNode.get("id");
        if (eventIdNode != null && eventIdNode.isNumber()) {
            int eventId = eventIdNode.intValue();
            recentEvents.put(eventId, eventNode);
            CompletableFuture<JsonNode> awaitingFuture = eventAwaitingFutures.get(eventId);
            if (awaitingFuture != null) {
                awaitingFuture.complete(eventNode);
            }
        }
    }

    public JsonNode getEventWithId(int eventId) throws TimeoutException {
        JsonNode cached = recentEvents.get(eventId);
        if (cached != null) {
            return cached;
        }
        CompletableFuture<JsonNode> future = new CompletableFuture<>();
        eventAwaitingFutures.put(eventId, future);
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("Event awaiting future thrown unexpected exception", e);
        } finally {
            eventAwaitingFutures.remove(eventId, future);
        }
    }
}
