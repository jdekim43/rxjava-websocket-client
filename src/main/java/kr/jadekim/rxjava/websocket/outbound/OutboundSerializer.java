package kr.jadekim.rxjava.websocket.outbound;

import java.util.Map;

public interface OutboundSerializer {

    String serialize(String messageType, Map<String, Object> parameterMap);
}
