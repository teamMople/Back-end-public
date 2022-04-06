package com.hanghae99.boilerplate.chat.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
public class ChatEntry {

    protected ChatEntry() {}

    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id
    private Long id;

    private Long memberId;
    private Long roomId;

    public ChatEntry(Long memberId, Long roomId) {
        this.memberId = memberId;
        this.roomId = roomId;
    }

}
