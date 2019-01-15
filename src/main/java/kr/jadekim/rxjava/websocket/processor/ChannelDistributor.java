package kr.jadekim.rxjava.websocket.processor;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import kr.jadekim.rxjava.websocket.filter.ChannelFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChannelDistributor<Model> {

    private String channel;
    private Observable<Model> channelStream;
    private Map<Integer, ChannelStream<Model>> channelStreamMap;

    public ChannelDistributor(String channel, Observable<Model> channelStream) {
        this.channel = channel;
        this.channelStream = channelStream;
        this.channelStreamMap = new HashMap<Integer, ChannelStream<Model>>();
    }

    public ChannelStream<Model> getStream(final ChannelFilter filter) {
        final int key = Arrays.hashCode(new int[] {filter.getClass().getName().hashCode(), filter.getParameterMap().hashCode()});
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
