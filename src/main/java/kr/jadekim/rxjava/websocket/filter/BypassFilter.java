package kr.jadekim.rxjava.websocket.filter;

import java.util.Map;

public class BypassFilter extends ChannelFilter {

    public BypassFilter(String channel, Map<String, Object> parameters) {
        super(channel, parameters);
    }

    @Override
    public boolean doFilter(String message, Object data) {
        return true;
    }
}
