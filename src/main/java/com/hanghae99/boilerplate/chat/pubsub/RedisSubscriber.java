package com.hanghae99.boilerplate.chat.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.chat.dto.ChatMessageDto;
import com.hanghae99.boilerplate.chat.model.ChatVote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(String publishMessage) {

        log.info("publishMessage : {}", publishMessage); // 여기서 구분해서 objectMapper 다르게 일하도록? 클래스를 chatMessage or chatVote로.

        if (publishMessage.contains("\"purpose\":\"VOTE\"")) {
            log.info("이것의 purpose 는 VOTE");
            try {
                ChatVote chatVote = objectMapper.readValue(publishMessage, ChatVote.class);
                messagingTemplate.convertAndSend("/sub/chat/vote/" + chatVote.getRoomId(), chatVote);
            } catch (Exception e) {
                log.error("RedisSubscriber에서 ChatVote 전송에서 exception 발생 :{}", e.getMessage());
            }

        } else {
            log.info("이것의 purpose 는 MESSAGE");
            try {
                ChatMessageDto chatMessageDto = objectMapper.readValue(publishMessage, ChatMessageDto.class);
                messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto);
            } catch (Exception e) {
                log.error("RedisSubscriber에서 ChatMessageDto 전송에서 exception 발생 :{}", e.getMessage());
            }
        }

    }

}
