package com.hanghae99.boilerplate.chat.model;

import com.hanghae99.boilerplate.chat.dto.ChatMessageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    private String sentAt;
    private ChatRole role;
    private Boolean agreed;
    private Boolean disagreed;

    public ChatMessage(ChatMessageDto chatMessageDto) {
        roomId = chatMessageDto.getRoomId();
        sender = chatMessageDto.getSender();
        message = chatMessageDto.getMessage();
        sentAt = chatMessageDto.getSentAt();
        role = chatMessageDto.getRole();
        agreed = chatMessageDto.getAgreed();
        disagreed = chatMessageDto.getDisagreed();
    }

}
