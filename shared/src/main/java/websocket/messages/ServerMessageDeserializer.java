package websocket.messages;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public final class ServerMessageDeserializer implements JsonDeserializer<ServerMessage> {
    @Override
    public ServerMessage deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
        if (!el.isJsonObject()) {
            return null;
        }
        String messageType = el.getAsJsonObject().get("serverMessageType").getAsString();
        return ctx.deserialize(el, switch (ServerMessage.ServerMessageType.valueOf(messageType)) {
            case NOTIFICATION -> Notification.class;
            case LOAD_GAME    -> LoadGameMessage.class;
            case ERROR        -> ErrorMessage.class;
        });
    }
}