package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;
import kr.jadekim.rxjava.websocket.inbound.Inbound;
import kr.jadekim.rxjava.websocket.inbound.InboundParser;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class StreamRouter {

    private InboundParser<Object> parser;
    private Observable<Inbound<Object>> mainStream;
    private Map<Integer, ChannelDistributor> distributorMap;

    StreamRouter(final InboundParser<Object> parser, Observable<String> inboundStream) {
        this.parser = parser;

        this.mainStream = inboundStream
                .map(new Function<String, Inbound<Object>>() {

                    @Override
                    public Inbound<Object> apply(String s) throws Exception {
                        return parser.parse(s);
                    }
                })
                .share();

        this.distributorMap = new HashMap<Integer, ChannelDistributor>();
    }

    @SuppressWarnings("unchecked")
    <Model> ChannelStream<Model> getStream(final String channel, final Type modelType, ChannelFilter filter) {
        final int key = Arrays.hashCode(new int[]{channel.hashCode(), modelType.hashCode()});
        ChannelDistributor<Model> distributor = distributorMap.get(key);

        if (distributor == null) {
            Observable<Model> stream = mainStream
                    .filter(new Predicate<Inbound<Object>>() {

                        @Override
                        public boolean test(Inbound<Object> objectInbound) throws Exception {
                            return channel.trim().length() == 0 || objectInbound.getChannel().equals(channel);
                        }
                    })
                    .map(new Function<Inbound<Object>, Model>() {

                        @Override
                        public Model apply(Inbound<Object> objectInbound) throws Exception {
                            return (Model) parser.mapping(objectInbound.getData(), modelType);
                        }
                    });

            distributor = new ChannelDistributor<Model>(channel, stream);
            distributorMap.put(key, distributor);
        }

        return distributor.getStream(filter);
    }
}
