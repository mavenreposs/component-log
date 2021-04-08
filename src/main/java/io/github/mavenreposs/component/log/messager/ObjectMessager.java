package io.github.mavenreposs.component.log.messager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.mavenreposs.component.log.contracts.MessagerInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ObjectMessager implements MessagerInterface {

    /**
     * Json缩紧
     */
    private static final int JSON_INDENT = 4;

    private final Object obj;

    public ObjectMessager(Object obj) {
        this.obj = obj;
    }

    @Override
    public String getParseMessage() {
        if (obj == null) {
            return "Null object content";
        }

        try {
            if (obj instanceof List) {
                JSONArray jsonArray = new JSONArray();
                for (Object o : (List) obj) {
                    JSONObject jo = new JSONObject(new Gson().toJson(o));
                    jsonArray.put(jo);
                }
                return jsonArray.toString(JSON_INDENT);
            } else if (obj instanceof Map) {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                JSONObject jsonObject = new JSONObject(gson.toJson(obj));
                return jsonObject.toString(JSON_INDENT);
            } else {
                JSONObject jsonObject = new JSONObject(new Gson().toJson(obj));
                return jsonObject.toString(JSON_INDENT);
            }
        } catch (JSONException e) {
            return "Invalid object content";
        }
    }
}
