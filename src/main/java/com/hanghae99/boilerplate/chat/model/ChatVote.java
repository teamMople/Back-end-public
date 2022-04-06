package com.hanghae99.boilerplate.chat.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatVote {

    private String purpose = "VOTE";

    // 메시지 타입 : 입장, 채팅, 퇴장 .. 찬반 및 취소
    public enum VoteType {
        AGREE, CANCEL_AGREE, DISAGREE, CANCEL_DISAGREE
    }
    private VoteType type; // 메시지 타입
    private Long roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private Long agreeCount;
    private Long disagreeCount;
    private Boolean agreedBefore;
    private Boolean disagreedBefore;
}
