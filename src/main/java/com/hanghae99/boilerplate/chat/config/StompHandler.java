package com.hanghae99.boilerplate.chat.config;

import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

// import ... 생략

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MemberRepository memberRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        //command가 null일 경우를 무시해야..
        Optional<StompCommand> command = Optional.ofNullable(accessor.getCommand());
        if (command.get() != null) {
            if (StompCommand.CONNECT == command.get()) {
                // 만약 매 발행(pub)에 대해 헤더에 대한 토큰 확인을 통한 인가를 원하다면 여기에 추가할 것

                log.info("StompHandler: message.getPayload: {}", message.getPayload());
            }
        }

        return message;
    }

}
