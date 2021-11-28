package utils;

import com.google.gson.Gson;

public class JsonUtils {
    static Gson gson = new Gson();

    public static <T> T parse(String s, Class<T> cls) {
        return gson.fromJson(s, cls);
    }
}
