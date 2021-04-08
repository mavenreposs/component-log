package io.github.mavenreposs.component.log.messager;

import io.github.mavenreposs.component.log.LogUtil;
import io.github.mavenreposs.component.log.contracts.MessagerInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonMessager implements MessagerInterface {

    /**
     * Json缩紧
     */
    private static final int JSON_INDENT = 4;

    private String json;

    public JsonMessager(String json) {
        this.json = json;
    }

    @Override
    public String getParseMessage() {
        if (LogUtil.isEmpty(json)) {
            return "Empty/Null json content";
        }

        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.toString(JSON_INDENT);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                return jsonArray.toString(JSON_INDENT);
            } else {
                return "Invalid json content";
            }
        } catch (JSONException e) {
            return "Invalid json content";
        }
    }
}
