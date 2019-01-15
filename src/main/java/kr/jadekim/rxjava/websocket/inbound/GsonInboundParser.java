package kr.jadekim.rxjava.websocket.inbound;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public abstract class GsonInboundParser implements InboundParser<JsonElement> {

    private Gson gson;

    public GsonInboundParser(Gson gson) {
        this.gson = gson;
    }

    public abstract String getChannelName(JsonElement data);

    @Override
    public Inbound<JsonElement> parse(String message) {
        JsonElement data = gson.fromJson(message, JsonElement.class);
        String channel = getChannelName(data);
        return new Inbound<JsonElement>(channel, data);
    }

    @Override
    public <Model> Model mapping(JsonElement object, Type modelType) {
        if (modelType == String.class) {
            //noinspection unchecked
            return (Model) object.toString();
        }

        return gson.fromJson(object, modelType);
    }
}
