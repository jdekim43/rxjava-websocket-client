package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;
import kr.jadekim.rxjava.websocket.util.Objects;

import java.util.HashMap;
import java.util.Map;

class ChannelDistributor<Model> {

    private String channel;
    private Observable<Model> channelStream;
    private Map<Integer, ChannelStream<Model>> channelStreamMap;

    ChannelDistributor(String channel, Observable<Model> channelStream) {
        this.channel = channel;
        this.channelStream = channelStream.share();
        this.channelStreamMap = new HashMap<Integer, ChannelStream<Model>>();
    }

    ChannelStream<Model> getStream(final ChannelFilter filter) {
        final int key = Objects.hashCode(filter.getClass().getName().hashCode(), filter.getParameterMap().hashCode());
        ChannelStream<Model> stream = channelStreamMap.get(key);

        if (stream == null) {
            stream = new ChannelStream<Model>(channel, channelStream.filter(new Predicate<Model>() {

                @Override
                public boolean test(Model model) throws Exception {
                    return filter.doFilter("", model);
                }
            }));
            channelStreamMap.put(key, stream);
        }

        return stream;
    }
}
