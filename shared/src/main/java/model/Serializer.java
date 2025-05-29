package model;

import com.google.gson.Gson;

public final class Serializer {
    private static final Gson gson = new Gson();

    public static <T> T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static String serialize(Object object) {
        return gson.toJson(object);
    }
}
