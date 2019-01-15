package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import kr.jadekim.rxjava.websocket.httpclient.Connection;
import kr.jadekim.rxjava.websocket.outbound.OutboundSerializer;

import java.io.IOException;
import java.util.Map;

class MessageSender {

    private Connection connection;
    private OutboundSerializer serializer;
    private PublishSubject<MessageQueueItem> subject = PublishSubject.create();

    MessageSender(Connection connection, OutboundSerializer serializer) {
        this.connection = connection;
        this.serializer = serializer;

        //noinspection ResultOfMethodCallIgnored
        subject
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<MessageQueueItem>() {

                    @Override
                    public void accept(MessageQueueItem messageQueueItem) throws Exception {
                        messageQueueItem.run();
                    }
                });
    }

    Completable sendMessage(String messageType, Map<String, Object> parameterMap) {
        return Completable.create(new MessageQueueItem(messageType, parameterMap));
    }

    private class MessageQueueItem implements CompletableOnSubscribe {

        private String messageType;
        private Map<String, Object> parameterMap;

        private CompletableEmitter emitter;

        public MessageQueueItem(String messageType, Map<String, Object> parameterMap) {
            this.messageType = messageType;
            this.parameterMap = parameterMap;
        }

        @Override
        public void subscribe(CompletableEmitter emitter) throws Exception {
            this.emitter = emitter;
            subject.onNext(this);
        }

        public void run() {
            if (emitter == null || emitter.isDisposed()) {
                return;
            }

            String message = serializer.serialize(messageType, parameterMap);
            if (connection.sendMessage(message)) {
                emitter.onComplete();
            } else {
                emitter.onError(new IOException("Fail to send message : " + message));
            }
        }
    }
}
