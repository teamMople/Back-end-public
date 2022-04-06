//package com.hanghae99.boilerplate.RestDocs;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
//import com.hanghae99.boilerplate.memberManager.update.service.MemberUpdateService;
//import com.hanghae99.boilerplate.security.jwt.TokenFactory;
//import com.hanghae99.boilerplate.security.service.UserDetailsServiceImpl;
//import com.hanghae99.boilerplate.signupLogin.kakao.common.RegisterMember;
//import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
//import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringBootExceptionReporter;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//import javax.persistence.EntityManager;
//
//@AutoConfigureMockMvc
//@AutoConfigureRestDocs
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@ActiveProfiles("test")
//
//public class RestConfig {
//    @Autowired
//    TokenFactory tokenFactory;
//    @Autowired
//     MockMvc mockMvc;
//    @Autowired
//    ObjectMapper objectMapper;
//    @MockBean
//    SignupLoginService signupLoginService;
//    @MockBean
//    KakaoLoginService kakaoLoginService;
//    @MockBean
//    RegisterMember registerMember;
//    @MockBean
//    UserDetailsServiceImpl userDetailsService;
//    @MockBean
//    PasswordEncoder passwordEncoder;
//    @MockBean
//    MemberUpdateService memberUpdateService;
//    @Autowired
//    EntityManager em;
//    @Autowired
//    MemberRepository memberRepository;
//
//}
