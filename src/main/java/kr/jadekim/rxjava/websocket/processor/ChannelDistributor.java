package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;
import kr.jadekim.rxjava.websocket.inbound.Inbound;
import kr.jadekim.rxjava.websocket.inbound.InboundParser;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;
import kr.jadekim.rxjava.websocket.util.Objects;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class ChannelDistributor<Model> {

    private String channel;
    private Observable<Model> channelStream;
    private Map<Integer, ChannelStream<Model>> channelStreamMap;
    private WebSocketEventListener listener;

    ChannelDistributor(final String channel, Observable<Inbound<Object>> mainStream, final InboundParser<Object> parser, final Type modelType, final WebSocketEventListener listener) {
        this.channel = channel;
        this.channelStream = mainStream
                .filter(new Predicate<Inbound<Object>>() {

                    @Override
                    public boolean test(Inbound<Object> objectInbound) throws Exception {
                        return channel.trim().length() == 0 || objectInbound.getChannel().equals(channel);
                    }
                })
                .map(new Function<Inbound<Object>, Model>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public Model apply(Inbound<Object> objectInbound) throws Exception {
                        return (Model) parser.mapping(objectInbound.getData(), modelType);
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onStartChannelStream(channel);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onErrorChannelStream(channel, throwable);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        listener.onStopChannelStream(channel);
                    }
                })
                .share();
        this.channelStreamMap = new HashMap<Integer, ChannelStream<Model>>();
        this.listener = listener;
    }

    ChannelStream<Model> getStream(ChannelFilter filter) {
        final int key = Objects.hashCode(filter.getClass().getName().hashCode(), filter.getParameterMap().hashCode());
        ChannelStream<Model> stream = channelStreamMap.get(key);

        if (stream == null) {
            stream = new ChannelStream<Model>(channel, channelStream, filter, listener);
            channelStreamMap.put(key, stream);
        }

        return stream;
    }
}
