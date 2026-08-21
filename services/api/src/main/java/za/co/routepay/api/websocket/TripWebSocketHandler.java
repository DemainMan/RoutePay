package za.co.routepay.api.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class TripWebSocketHandler {

    @MessageMapping("/trip.update")
    @SendTo("/topic/trips")
    public Map<String, Object> handleTripUpdate(Map<String, Object> payload) {
        return payload;
    }

    @MessageMapping("/location.update")
    @SendTo("/topic/locations")
    public Map<String, Object> handleLocationUpdate(Map<String, Object> payload) {
        return payload;
    }
}
