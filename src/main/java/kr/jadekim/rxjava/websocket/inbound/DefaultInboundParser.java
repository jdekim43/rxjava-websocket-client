package kr.jadekim.rxjava.websocket.inbound;

import java.lang.reflect.Type;

public class DefaultInboundParser implements InboundParser<String> {

    @Override
    public Inbound<String> parse(String message) {
        return new Inbound<String>("", message);
    }

    @Override
    public <Model> Model mapping(String object, Type modelType) {
        if (modelType == String.class) {
            //noinspection unchecked
            return (Model) object;
        }

        return null;
    }
}
