package com.hanghae99.boilerplate.chat.dto;

import com.hanghae99.boilerplate.chat.model.ChatRole;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChatCloseDto {
    @NotNull(message = "roomId는 null일 수 없습니다.")
    private Long roomId;
    @NotNull(message = "memberName는 null일 수 없습니다.")
    private String memberName;
    private ChatRole role;
}
