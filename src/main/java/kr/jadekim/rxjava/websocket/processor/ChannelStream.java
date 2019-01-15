package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.*;

public class ChannelStream<Model> {

    private String channel;
    private Observable<Model> stream;

    public ChannelStream(String channel, Observable<Model> stream) {
        this.channel = channel;
        this.stream = stream.share();
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
