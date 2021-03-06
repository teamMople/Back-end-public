//package com.hanghae99.boilerplate.RestDocs;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.api.client.json.Json;
//import com.hanghae99.boilerplate.integrationTest.Config;
//import com.hanghae99.boilerplate.memberManager.mail.OnlyEmailDto;
//import com.hanghae99.boilerplate.memberManager.mail.service.MailService;
//import com.hanghae99.boilerplate.memberManager.model.Member;
//import com.hanghae99.boilerplate.memberManager.model.Role;
//import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
//import com.hanghae99.boilerplate.memberManager.update.UpdateMemberDto;
//import com.hanghae99.boilerplate.memberManager.update.service.MemberUpdateService;
//import com.hanghae99.boilerplate.security.config.JwtConfig;
//import com.hanghae99.boilerplate.security.jwt.TokenFactory;
//import com.hanghae99.boilerplate.security.model.MemberContext;
//import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
//import com.hanghae99.boilerplate.security.service.UserDetailsImpl;
//import com.hanghae99.boilerplate.security.service.UserDetailsServiceImpl;
//import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
//import com.hanghae99.boilerplate.signupLogin.kakao.common.RegisterMember;
//import com.hanghae99.boilerplate.signupLogin.kakao.common.WebUtil;
//import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
//import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
//import org.elasticsearch.monitor.os.OsStats;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.restdocs.RestDocumentationExtension;
//import org.springframework.restdocs.payload.JsonFieldType;
//import org.springframework.restdocs.payload.PayloadDocumentation;
//import org.springframework.restdocs.payload.PayloadDocumentation.*;
//import org.springframework.restdocs.snippet.Snippet;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//
//import javax.persistence.EntityManager;
//import javax.swing.*;
//import java.rmi.registry.Registry;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.fileUpload;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//public class Signup extends RestConfig {
//
//
//    @Test
//    void signUP() throws Exception {
//        List<GrantedAuthority> userRole = new ArrayList<>();
//        userRole.add(new SimpleGrantedAuthority(Role.ROLE_USER));
//        final SignupReqestDto signupReqestDto = new SignupReqestDto("email@naver.com", "nickname", "profileImageUrl", "password");
//        Mockito.when(signupLoginService.signupRequest(any())).thenReturn(new MemberContext("email", userRole, "nickname", 13L, "image"));
//
//        mockMvc.perform(post("/api/signup")
//                        .content(objectMapper.writeValueAsString(signupReqestDto))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(document("signupRequest",
//                        requestFields(
//                                fieldWithPath("email").description("????????? ???????????? ??????"),
//                                fieldWithPath("nickname").description("???????????? ??????"),
//                                fieldWithPath("password").description("???????????? ?????? ??????"),
//                                fieldWithPath("profileImageUrl").description("????????? URL??? ??????")
//                        )));
//    }
//
//    @Test
//    void checkDuplicatesEmail() throws Exception {
//        Mockito.when(signupLoginService.DuplicatesEmail(any())).thenReturn(true);
//        mockMvc.perform(post("/api/user/check/Email")
//                        .content("{\"email\":\"email@naver.com\"}")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(document("DuplicatesEmail",
//                        requestFields(
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????????????? ?????????")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("true or false")
//                        )));
//    }
//
//    @Test
//    void checkDuplicatesNickname() throws Exception {
//        Mockito.when(signupLoginService.DuplicateNickname(any())).thenReturn(true);
//        mockMvc.perform(post("/api/user/check/Nickname")
//                        .content("{\"nickname\":\"hojun\"}")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(document("DuplicatesNickname",
//                        requestFields(
//                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????????????? ?????????")
//                        ),
//                        responseFields(
//                                fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("true or false")
//                        )));
//    }
//
//    @Test
//    void logout() throws Exception {
//        Mockito.doNothing().when(signupLoginService).logoutRequest(any());
//        mockMvc.perform(post("/api/logout")
//                        .header("Authorization", JwtConfig.TOKEN_TYPE + "dXNlcjpzZWNyZXQ="))
//                .andDo(print())
//                .andDo(document("logout",
//                        responseFields(
//                                fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("???????????? ??????")))
//                        );
//
//                ;
//    }
//
//    @Test
//    void login() throws Exception {
//        Set<String> roles = new HashSet<>();
//        roles.add(Role.ROLE_USER);
//        LoginRequestDto loginRequestDto = new LoginRequestDto("email@naver.com", "123456");
//
//        Mockito.when(passwordEncoder.matches(any(), any())).thenReturn(true);
//        Mockito.when(userDetailsService.loadUserByUsername(any())).thenReturn(new UserDetailsImpl(loginRequestDto.getEmail(), loginRequestDto.getPassword()
//                , WebUtil.stringToGrantedAuthority(Role.ROLE_USER), "nickname", 1L, "Image"));
//
//        mockMvc.perform(post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequestDto)))
//                .andExpect(status().isOk())
//                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
//                .andExpect(cookie().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
//                .andDo(document("login",
//                        requestFields(
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("email@naver.com"),
//                                fieldWithPath("password").type(JsonFieldType.STRING).description("password12314!")),
//                        responseFields(
//                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("??????????????? ?????????"),
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("??????????????? ?????????"),
//                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("????????? ?????????")
//                        )));
//    }
//
//
//
//    @Test
//    @Transactional
//    void ????????????????????????() throws Exception {
//        UpdateMemberDto updateMemberDto = new UpdateMemberDto("nickname", "imageurl");
//        MemberContext memberContext= new MemberContext("email@naver.com", WebUtil.stringToGrantedAuthority(Role.ROLE_USER), "nickname", 13L, "image");
//        Mockito.when(memberUpdateService.updateMemberInformation(any(),any())).thenReturn(memberContext);
//        mockMvc.perform(MockMvcRequestBuilders.post("/auth/api/user/update")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(JwtConfig.AUTHENTICATION_HEADER_NAME, JwtConfig.TOKEN_TYPE + tokenFactory.createToken(memberContext, 10000).getToken())
//                        .content(objectMapper.writeValueAsString(updateMemberDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(document("updateMember",
//                        requestFields(
//                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
//                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("????????? ?????????")),
//                        responseFields(
//                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
//                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("????????? ?????????"),
//                                fieldWithPath("email").type(JsonFieldType.STRING).description("????????? ?????????")
//                        )
//                ));
//        ;
//    }
//
//}
