package com.hanghae99.boilerplate.chat.dto;

import com.hanghae99.boilerplate.chat.model.ChatRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {

    private String purpose = "MESSAGE";

    // 메시지 타입 : 입장, 채팅, 퇴장 .. 찬반 및 취소
    public enum MessageType {
        ENTER, CHAT, LEAVE
    }
    private MessageType type; // 메시지 타입
    private Long roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    private String sentAt;
    private String profileUrl;
    private ChatRole role;
    private Boolean agreed;
    private Boolean disagreed;
}
