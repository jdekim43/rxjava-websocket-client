package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import kr.jadekim.rxjava.websocket.inbound.Inbound;
import kr.jadekim.rxjava.websocket.inbound.InboundParser;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class StreamRouter {

    private InboundParser<Object> parser;
    private Observable<Inbound<Object>> mainStream;
    private Map<Integer, ChannelDistributor> distributorMap;
    private WebSocketEventListener listener;

    StreamRouter(final InboundParser<Object> parser, Observable<String> inboundStream, final WebSocketEventListener listener) {
        this.parser = parser;

        this.mainStream = inboundStream
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onStartInboundStream();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onErrorInboundStream(throwable);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        listener.onStopInboundStream();
                    }
                })
                .map(new Function<String, Inbound<Object>>() {

                    @Override
                    public Inbound<Object> apply(String s) throws Exception {
                        return parser.parse(s);
                    }
                })
                .share();

        this.distributorMap = new HashMap<Integer, ChannelDistributor>();
        this.listener = listener;
    }

    <Model> ChannelStream<Model> getStream(Subscription subscription) {
        final String channel = subscription.getChannel();
        final Type modelType = subscription.getResponseType();
        final int key = subscription.getChannelId();
        //noinspection unchecked
        ChannelDistributor<Model> distributor = distributorMap.get(key);

        if (distributor == null) {
            distributor = new ChannelDistributor<Model>(channel, mainStream, parser, modelType, listener);
            distributorMap.put(key, distributor);
        }

        return distributor.getStream(subscription);
    }
}
