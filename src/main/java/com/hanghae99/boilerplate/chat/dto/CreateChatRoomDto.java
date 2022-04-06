package com.hanghae99.boilerplate.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
@Setter
public class CreateChatRoomDto {
    @NotNull
    private String roomName;
    @NotNull
    private String category;
    @NotNull
    private String moderator;
    @NotNull
    private Long maxParticipantCount;
    @NotNull
    private String content;

    public CreateChatRoomDto(String roomName, String category, String moderator, Long maxParticipantCount, String content, Boolean isPrivate) {
        this.roomName = roomName;
        this.category = category;
        this.moderator = moderator;
        this.maxParticipantCount = maxParticipantCount;
        this.content = content;
    }
}
