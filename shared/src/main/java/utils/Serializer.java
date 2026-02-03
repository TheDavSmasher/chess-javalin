package utils;

import com.google.gson.*;
import websocket.commands.*;
import websocket.messages.*;

import java.lang.reflect.Type;

public final class Serializer {
    public static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer());
        gsonBuilder.registerTypeAdapter(UserGameCommand.class, new UserCommandDeserializer());

        gson = gsonBuilder.create();
    }

    public static <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static String serialize(Object object) {
        return gson.toJson(object);
    }


    public static abstract class Deserializer<T> implements JsonDeserializer<T> {
        protected abstract String enumField();
        protected abstract Class<? extends T> enumClass(String value);

        @Override
        public T deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) {
            if (!el.isJsonObject()) {
                return null;
            }
            String enumVal = el.getAsJsonObject().get(enumField()).getAsString();
            return ctx.deserialize(el, enumClass(enumVal));
        }
    }
}
