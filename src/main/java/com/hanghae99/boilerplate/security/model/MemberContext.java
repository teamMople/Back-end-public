package com.hanghae99.boilerplate.security.model;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.signupLogin.kakao.common.WebUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberContext {
    //principal로 catsing 되는 객체
    private String username;
    private List<GrantedAuthority> authorities;
    private String nickname;
    private Long memberId;
    private String profileImageUrl;
    public MemberContext(Member member) {
        this.username = member.getEmail();
        this.nickname = member.getNickname();
        this.authorities = WebUtil.stringToGrantedAuthority(member.getRoles());
        this.memberId = member.getId();
        this.profileImageUrl= member.getProfileImageUrl();
    }

    public static MemberContext create(String email, List<GrantedAuthority> authorities, String nickname, Long id,String profileImageUrl) {
        return new MemberContext(email, authorities, nickname, id,profileImageUrl);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
