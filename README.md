# rxjava-websocket-client
[ ![Download](https://api.bintray.com/packages/jdekim43/maven/rxjava-websocket-client/images/download.svg?version=0.0.1) ](https://bintray.com/jdekim43/maven/rxjava-websocket-client/0.0.1/link)


### Features
* Usable any (real connection) Client : OkHttp, etc...  
&nbsp;&nbsp;&nbsp;&nbsp; OkHttp is implemented
* Acceptable any message type based on String : JSON, XML, TEXT  
&nbsp;&nbsp;&nbsp;&nbsp; Implementation of InboundParser and OutboundSerializer is required.
* Fabricable 'Observable' which makes 'received message' events you need by using channel and filters.


### Dependency
* RxJava2
* OkHttp (Optional)


### Install
```
compile "kr.jadekim.rxjava:rxjava-websocket-client:$version"

//or

implementation "kr.jadekim.rxjava:rxjava-websocket-client:$version"
```

### Define client interface
#### Use builder class
```
public interface TestClient {
    
    @Channel
    Flowable<String> subscribeAll();
    
    @Channel(value="channel1", filter=RemainderFilter.class)
    Observable<ResponseModel> subscribeChannel1(@Name("dividerValue") int dividerValue,
                                                @Name("remainderValue") int remainderValue);
    
    @Channel(
        value="channel2",
        filter=RemainderFilter.class,
        onStart="requestNumber2(interval, start)",
        onStop="stopNumber2(interval, start)"
    )
    @Params(
        @Param(name="interval", value="1")
    )
    Flowable<ResponseModel> subscribeChannel2(@Name("start") int start,
                                              @Name("dividerValue") int dividerValue,
                                              @Name("remainderValue") int remainderValue);
    
    @Message
    Completable sendMessage(String message);
    
    @Message("requestNumber")
    Completable requestNumber(@Name("interval") int interval);
    
    @Message("requestNumber")
    Completable requestNumber2(@Name("interval") int interval,
                               @Name("startNumber") int startNumber);
    
    @Message("stopNumber")
    Completable stopNumber2(@Name("interval") int interval,
                            @Name("startNumber") int startNumber);
    
    // If method name is 'disconnect', it will be working 'disconnect' action
    void disconnect();
}
```
```
JWebSocket webSocketClientFactory = new JWebSocket.Builder().build();
TestClient client = webSocketClientFactory.create(TestClient.class, "ws://localhost/ws");
client.subscribeAll().subscribe(msg -> System.out.println(msg));
```

#### Use annotation
```
@WebSocketClient(
    url = "wss://echo.websocket.org"
)
public interface TestClient {...}

TestClient client = JWebSocket.create(TestClient.class);
```


### Customize
#### Customize ConnectionFactory (Customize real connection Client)
```
public class OkHttpConnectionFactory implements ConnectionFactory {
    ...
    public OkHttpConnectionFactory(OkHttpClient okHttpClient) {
        ...
    }
    
    @Override
    public Connection connect(String url, boolean isErrorPropagation, WebSocketEventListener listener) {
        ...
    }
}
```
```
OkHttpClient httpClient = new OkHttpClient();
ConnectionFactory connectionFactory = new OkHttpConnectionFactory(httpClient);
```

#### Customize Inbound Message Parser
```
public abstract class GsonInboundParser implements InboundParser<JsonElement> {
    ...
    public GsonInboundParser(Gson gson) {
        ...
    }
    
    public abstract String getChannelName(JsonElement data);
    
    @Override
    public Inbound<JsonElement> parse(String message) {
        ...
    }
    
    @Override
    public <Model> Model mapping(JsonElement object, Type modelType) {
        ...
    }
}
```
```
Gson gson = new Gson();

InboundParser parser = new GsonInboundParser(gson) {

    @Override
    public String getChannelName(JsonElement data) {
        return data.getAsJsonObject().get("channel").getAsString(); 
    }
};
```

#### Customize Outbound Message Serializer
```
public class GsonOutboundSerializer implements OutboundSerializer {
    ...
    public GsonOutboundSerializer(Gson gson) {
        ...
    }

    @Override
    public String serialize(String messageType, Map<String, Object> parameterMap) {
        ...
    }
}
```
```
Gson gson = new Gson();
OutboundSerializer serializer = new GsonOutboundSerializer(gson);
```

### Configuration
#### Use builder class
```
JWebSocket webSocketClientFactory = new JWebSocket.Builder()
                .connectionFactory(connectionFactory)
                .parser(parser)
                .serializer(serializer)
                .lazy(false)
                .errorPropagation(true)
                .build();
                
TestClient client = webSocketClientFactory.create(TestClient.class, "ws://localhost/ws");
```

#### Use annotation
```
@WebSocketClient(
    url = "wss://echo.websocket.org",
    connectionFactory = OkHttpConnectionFactory.class,
    parser = DefaultInboundParser.class,
    serializer = DefaultOutboundSerializer.class,
    listener = SimpleWebSocketEventListener.class,
    isLazy = false,
    isErrorPropagation = true
)
public interface TestClient {...}

TestClient client = JWebSocket.create(TestClient.class);
```

#### Lazy (default : false)
When this option is enabled, the 'ConnectionFactory' specified by the user is wrapped in 'LazyConnectionFactory'.
'LazyConnection' created by 'LazyConnectionFactory' automatically terminates the connection when there is no 'Observable' subscribing to the connection.
And if the 'sendMessage' method is called, or if there is one or more subscription 'Observable', it automatically creates a connection.

#### Error propagation (default : true)
This option allows you to set whether or not to notify 'Observable' of errors that occur on the connection.
'Observable' in 'RxJava' will automatically terminate the stream when 'onError' is called.
If you want to restore the connection and re-use 'Observable', disable this option.

### Inbound message flow chart and event timing
![DataFlow](./data-flow.png?raw=true "DataFlow")
