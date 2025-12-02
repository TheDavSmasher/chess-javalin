package websocket.commands;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public final class UserCommandDeserializer implements JsonDeserializer<UserGameCommand> {
    @Override
    public UserGameCommand deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
        if (!el.isJsonObject()) {
            return null;
        }
        String commandType = el.getAsJsonObject().get("commandType").getAsString();
        return ctx.deserialize(el, switch (UserGameCommand.CommandType.valueOf(commandType)) {
            case CONNECT -> ConnectCommand.class;
            case LEAVE -> LeaveCommand.class;
            case RESIGN -> ResignCommand.class;
            case MAKE_MOVE -> MakeMoveCommand.class;
        });
    }
}
