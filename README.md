# rxjava-websocket-client


### 주요기능
* 자유로운 HttpClient : 모든 HttpClient 에서 동작할 수 있습니다. (Connection/ConnectionFactory 구현, OkHttp 는 OkHttpConnectionFactory 를 통해 바로 사용할 수 있습니다.)
* 자유로운 메시지 형식 및 클래스 매핑 : json/xml/PlainString 등 자유로운 메시지 형식에 자유롭게 파서/매퍼를 설정할 수 있습니다. (InboundParser/OutboundSerializer 구현)
* 하나의 Connection Inbound 스트림의 채널 및 커스텀 필터를 이용한 Observable 분리
* LazyConnection : 구독하는 스트림이 있거나 sendMessage 요청시 WebSocket Connection 을 맺음 (모든 스트림이 dispose 되면 disconnect. 단, sendMessage 로 요청 후 dispose 되는 스트림이 없을 경우 disconnect 되지 않음.)


### 의존 라이브러리
*  RxJava2


### 사용방법
##### 설치
maven/gradle 추후 지원 예정

##### 인터페이스 정의
```
public interface TestClient {
    
    @Channel
    Flowable<String> subscribeAll();
    
    @Channel(value="channel1", filter=RemainderFilter.class)
    Observable<ResponseModel> subscribeChannel1(@Name("dividerValue") int dividerValue, @Name("remainderValue") int remainderValue);
    
    @Channel(value="channel2", filter=RemainderFilter.class, onStart="requestNumber2(interval, start)", onStop="stopNumber2(interval, start)")
    @Params(
        @Param(name="interval", value="1")
    )
    Flowable<ResponseModel> subscribeChannel2(@Name("start") int start, @Name("dividerValue") int dividerValue, @Name("remainderValue") int remainderValue);
    
    @Message
    Completable sendMessage(String message);
    
    @Message("requestNumber")
    Completable requestNumber(@Name("interval") int interval);
    
    @Message("requestNumber")
    Completable requestNumber2(@Name("interval") int interval, @Name("startNumber") int startNumber);
    
    @Message("stopNumber")
    Completable stopNumber2(@Name("interval") int interval, @Name("startNumber") int startNumber);
    
    // 메소드 명을 'disconnect' 로 하고 인스턴스로 만들어 호출 시 웹소켓 연결을 끊음.
    void disconnect();
}
```

##### ConnectionFactory 생성
```
OkHttpClient httpClient = new OkHttpClient();
ConnectionFactory connectionFactory = new OkHttpConnectionFactory(httpClient);
```

##### Parser/Serialiser 구현
```
Gson gson = new Gson();

InboundParser parser = new GsonInboundParser(gson) {

    @Override
    public String getChannelName(JsonElement data) {
        return object.getAsJsonObject().get("channel").getAsString(); 
    }
};


OutboundSerializer serializer = new GsonOutboundSerializer(gson);
```

##### WebSocketClient 인스턴스 생성
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

##### Annotation 으로 WebSocketClient 설정
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


### 스트림 데이터 플로우 및 이벤트 시점
![DataFlow](./data-flow.png?raw=true "DataFlow")
