package websocket.messages;

import utils.Serializer;

public final class ServerMessageDeserializer extends Serializer.Deserializer<ServerMessage> {
    @Override
    protected String enumField() {
        return "serverMessageType";
    }

    @Override
    protected Class<? extends ServerMessage> enumClass(String value) {
        return switch (ServerMessage.ServerMessageType.valueOf(value)) {
            case NOTIFICATION -> Notification.class;
            case LOAD_GAME    -> LoadGameMessage.class;
            case ERROR        -> ErrorMessage.class;
        };
    }
}