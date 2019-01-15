package kr.jadekim.rxjava.websocket.filter;

import java.util.Collections;
import java.util.Map;

public abstract class ChannelFilter {

    private final String channel;
    private final Map<String, Object> parameterMap;

    public ChannelFilter(String channel, Map<String, Object> parameterMap) {
        this.channel = channel;
        this.parameterMap = Collections.unmodifiableMap(parameterMap);
    }

    public abstract boolean doFilter(String message, Object data);

    public String getChannel() {
        return channel;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }
}
