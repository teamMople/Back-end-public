package com.hanghae99.boilerplate.memberManager.update.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.memberManager.model.ResponseDto;
import com.hanghae99.boilerplate.memberManager.update.UpdateMemberDto;
import com.hanghae99.boilerplate.memberManager.update.service.MemberUpdateService;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.kakao.common.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class MemberUpdateController {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MemberUpdateService memberUpdateService;
    @PostMapping("/api/user/update")
    public void updateMemberInformation(HttpServletResponse response, @AuthenticationPrincipal MemberContext memberContext, @RequestBody UpdateMemberDto updateMemberDto) throws IOException {
        MemberContext newMemberContext=memberUpdateService.updateMemberInformation(memberContext,updateMemberDto);
        objectMapper.writeValue(response.getWriter(), WebUtil.UserDataToMap(newMemberContext,""));

    }

    @PostMapping("/api/user/inactivate")
    public void DeleteMember(HttpServletResponse response,@AuthenticationPrincipal MemberContext memberContext) throws IOException {
        memberUpdateService.deleteMember(memberContext);
        objectMapper.writeValue(response.getWriter(), ResponseDto.of(HttpStatus.OK,"inactivate success",null));

    }
}
