package com.hanghae99.boilerplate.memberManager.update;

import lombok.*;

import javax.persistence.Access;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateMemberDto {
    private String nickname;
    private String profileImageUrl;

}
