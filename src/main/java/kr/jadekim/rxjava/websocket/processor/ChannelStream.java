package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;

public class ChannelStream<Model> {

    private String channel;
    private Observable<Model> stream;

    public ChannelStream(final String channel, Observable<Model> stream, final ChannelFilter filter, final WebSocketEventListener listener) {
        this.channel = channel;
        this.stream = stream
                .filter(new Predicate<Model>() {

                    @Override
                    public boolean test(Model model) throws Exception {
                        return filter.doFilter("", model);
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onStartFilteredStream(channel, filter);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onErrorFilteredStream(channel, filter, throwable);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        listener.onStopFilteredStream(channel, filter);
                    }
                })
                .share()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        listener.onSubscribeFilteredStream(channel, filter);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        listener.onUnsubscribeFilteredStream(channel, filter);
                    }
                });

    }

    public String getChannel() {
        return channel;
    }

    public Observable<Model> asObservable() {
        return stream;
    }

    public Flowable<Model> asFlowable() {
        return stream.toFlowable(BackpressureStrategy.LATEST);
    }

    public Single<Model> asSingle() {
        return stream.firstOrError();
    }

    public Maybe<Model> asMaybe() {
        return stream.firstElement();
    }

    public Completable asCompletable() {
        return stream.ignoreElements();
    }
}
