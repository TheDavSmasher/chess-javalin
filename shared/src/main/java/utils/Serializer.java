package utils;

import com.google.gson.*;
import websocket.messages.*;

public final class Serializer {
    public static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer());

        gson = gsonBuilder.create();
    }

    public static <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static String serialize(Object object) {
        return gson.toJson(object);
    }
}
