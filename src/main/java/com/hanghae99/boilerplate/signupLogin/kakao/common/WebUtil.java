package com.hanghae99.boilerplate.signupLogin.kakao.common;

import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.model.MemberContext;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.Cookie;
import java.util.*;


public class WebUtil {

    public static Cookie makeCookie(String name ,String value){
        Cookie cookie = new Cookie(name,value);
//        cookie.setDomain("boiler-plate.org");
        cookie.setPath("/");
        cookie.setHttpOnly(true);

//        cookie.setSecure(true);
        return cookie;
    }

    public static Map<String,String> UserDataToMap(MemberContext memberContext,String token){
        Map<String, String> userData = new HashMap<String, String>();
        userData.put("nickname", memberContext.getNickname());
        userData.put("email", memberContext.getUsername());
        userData.put("profileImageUrl",memberContext.getProfileImageUrl());
        userData.put(JwtConfig.AUTHENTICATION_HEADER_NAME,token);

        return userData;
    }

    public static Collection<SimpleGrantedAuthority> stringToSimpleGrantedAuthority(String role){
        Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
    public static List<GrantedAuthority> stringToGrantedAuthority(String role){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
