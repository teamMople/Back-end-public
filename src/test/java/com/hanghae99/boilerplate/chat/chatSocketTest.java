//package com.hanghae99.boilerplate.chat;
//
//import com.hanghae99.boilerplate.chat.dto.ChatMessageDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.messaging.converter.StringMessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//
//import static com.hanghae99.boilerplate.chat.dto.ChatMessageDto.MessageType.CHAT;
//import static java.util.concurrent.TimeUnit.SECONDS;
//
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class chatSocketTest {
//
//    @LocalServerPort
//    private Integer port;
//
//    private WebSocketStompClient webSocketStompClient;
//
//    @BeforeEach
//    public void setup() {
//        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
//                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
//    }
//
//    @Test
//    public void verifyGreetingIsReceived() throws Exception {
//
//        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue(1);
//
//        webSocketStompClient.setMessageConverter(new StringMessageConverter());
//
//        StompSession session = webSocketStompClient
//                .connect(getWsPath(), new StompSessionHandlerAdapter() {})
//                .get(1, SECONDS);
//
//        session.subscribe("/sub/chat/room/11", new StompFrameHandler() {
//
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return String.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                System.out.println("Received message: " + payload);
//                blockingQueue.add((String) payload);
//            }
//        });
//
//        ChatMessageDto chatMessageDto = new ChatMessageDto();
//        chatMessageDto.setRoomId(11L);
//        chatMessageDto.setType(CHAT);
//        chatMessageDto.setMessage("hello");
//        chatMessageDto.setSender("bonobono");
//
//        session.send("/pub/chat/message", "{\"purpose\":\"MESSAGE\",\"roomId\":\"11\",\"type\":\"CHAT\",\"message\":\"hi\"}");
//
//        String poll = blockingQueue.poll(1, SECONDS);
//
//        System.out.println("poll = " + poll);
////
////        assertEquals("Hello, Mike!", blockingQueue.poll(1, SECONDS));
//    }
//
//    private String getWsPath() {
//        return String.format("ws://localhost:%d/api/ws-stomp/message", port);
//    }
//
//}
