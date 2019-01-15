package kr.jadekim.rxjava.websocket.inbound;

public class Inbound<ParsingClass> {

    private String channel;
    private ParsingClass data;

    public Inbound(String channel, ParsingClass data) {
        this.channel = channel;
        this.data = data;
    }

    public String getChannel() {
        return channel;
    }

    public ParsingClass getData() {
        return data;
    }
}
