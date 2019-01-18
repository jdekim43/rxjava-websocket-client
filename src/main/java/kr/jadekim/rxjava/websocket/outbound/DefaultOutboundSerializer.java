package kr.jadekim.rxjava.websocket.outbound;

import java.util.Map;

public class DefaultOutboundSerializer implements OutboundSerializer {

    @Override
    public String serialize(String messageType, Map<String, Object> parameterMap) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Object> each : parameterMap.entrySet()) {
            builder.append(each.getKey())
                    .append('=')
                    .append(each.getValue())
                    .append(';');
        }

        return builder.toString();
    }
}
