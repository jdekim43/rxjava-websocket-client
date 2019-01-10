package kr.jadekim.rxjava.websocket.filter;

import java.util.Collections;
import java.util.Map;

public abstract class ChannelFilter {

    private final String channel;
    private final Map<String, Object> parameters;

    public ChannelFilter(String channel, Map<String, Object> parameters) {
        this.channel = channel;
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public abstract boolean doFilter(String message, Object data);

    public String getChannel() {
        return channel;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
