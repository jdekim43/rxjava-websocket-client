package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import kr.jadekim.rxjava.websocket.connection.Connection;
import kr.jadekim.rxjava.websocket.listener.WebSocketEventListener;
import kr.jadekim.rxjava.websocket.outbound.OutboundSerializer;

import java.io.IOException;
import java.util.Map;

class MessageSender {

    private Connection connection;
    private OutboundSerializer serializer;
    private WebSocketEventListener listener;
    private PublishSubject<MessageQueueItem> subject = PublishSubject.create();

    MessageSender(Connection connection, OutboundSerializer serializer, WebSocketEventListener listener) {
        this.connection = connection;
        this.serializer = serializer;
        this.listener = listener;

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
            listener.onAddMessageQueue(messageType, parameterMap);
            subject.onNext(this);
        }

        public void run() {
            if (emitter == null || emitter.isDisposed()) {
                return;
            }

            String message = serializer.serialize(messageType, parameterMap);
            listener.onSendMessage(messageType, parameterMap, message);
            if (connection.sendMessage(message)) {
                listener.onCompleteSendMessage(messageType, parameterMap, message);
                emitter.onComplete();
            } else {
                listener.onErrorSendMessage(messageType, parameterMap, message);
                emitter.onError(new IOException("Fail to send message : " + message));
            }
        }
    }
}
