package net.polarizedions.polarizedbot.api.apiutil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {
    private static final JsonParser parser = new JsonParser();

    int code;
    Map<String, String> headers;
    String body;
    JsonElement parsedBody;

    @Contract(pure = true)
    public HTTPResponse() {
        this.code = 200;
        this.headers = new HashMap<>();
        this.body = "";
    }

    @Contract(pure = true)
    public HTTPResponse(int code, Map<String, String> headers, String body) {
        this.code = code;
        this.headers = headers;
        this.body = body;
    }

    public int getCode() {
        return this.code;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public String getBody() {
        return this.body;
    }

    public JsonElement asJson() {
        if (this.parsedBody == null) {
            this.parsedBody = parser.parse(this.getBody());
        }

        return this.parsedBody;
    }

    public JsonObject asJsonObject() {
        JsonElement json = this.asJson();
        return json == null ? null : json.getAsJsonObject();
    }
}
