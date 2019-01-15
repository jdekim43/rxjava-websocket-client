package kr.jadekim.rxjava.websocket.inbound;

import java.lang.reflect.Type;

public interface InboundParser<ParsingClass> {

    Inbound<ParsingClass> parse(String message);

    <Model> Model mapping(ParsingClass object, Type modelType);
}
