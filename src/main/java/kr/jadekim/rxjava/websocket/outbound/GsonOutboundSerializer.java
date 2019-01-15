package kr.jadekim.rxjava.websocket.outbound;

import com.google.gson.Gson;

import java.util.Map;

public class GsonOutboundSerializer implements OutboundSerializer {

    private Gson gson;

    public GsonOutboundSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String serialize(String messageType, Map<String, Object> parameterMap) {
        if (parameterMap == null) {
            return "";
        }

        if (parameterMap.keySet().size() == 1 && parameterMap.containsKey("0")) {
            Object data = parameterMap.get("0");

            if (data instanceof Number || data instanceof Boolean || data instanceof Character || data instanceof String) {
                return data.toString();
            }

            return gson.toJson(data);
        }

        return gson.toJson(parameterMap);
    }
}
