//package com.hanghae99.boilerplate.integrationTest;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.hanghae99.boilerplate.memberManager.model.Member;
//import com.hanghae99.boilerplate.memberManager.model.Role;
//import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
//import com.hanghae99.boilerplate.memberManager.update.UpdateMemberDto;
//import com.hanghae99.boilerplate.memberManager.update.service.MemberUpdateService;
//import com.hanghae99.boilerplate.security.config.JwtConfig;
//import com.hanghae99.boilerplate.security.model.MemberContext;
//import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
//import org.hibernate.AssertionFailure;
//import org.junit.BeforeClass;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.security.sasl.AuthenticationException;
//
//import java.util.NoSuchElementException;
//
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.mock;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//public class SignupTest extends Config {
//
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    EntityManager em;
//    SignupReqestDto normalSignupReqestDto = new SignupReqestDto("wns67431231@naver.com", "최호준", "1234", "이미지1번");
//    SignupReqestDto badSignupReqestDto = new SignupReqestDto("wns67431231@naver.com", "최호준", "", "이미지1번");
//    String email = "{ \"email\" : " + "\"" + normalSignupReqestDto.getEmail() + "\" }";
//
//
//    @BeforeEach
//    @Transactional
//    void before() {
//        memberRepository.deleteByEmail(normalSignupReqestDto.getEmail());
//
//    }
//
//    @AfterEach
//    @Transactional
//    void after() {
//        memberRepository.deleteByEmail(normalSignupReqestDto.getEmail());
//    }
//
//
//    @Test
//    @Transactional
//    void 회원가입성공() throws Exception {
//        mockMvc.perform(post("/api/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("email").exists())
//                .andExpect(jsonPath("profileImageUrl").exists())
//                .andExpect(jsonPath("nickname").exists());
//        Assertions.assertEquals(memberRepository.findByEmail(normalSignupReqestDto.getEmail()).get().getNickname(), normalSignupReqestDto.getNickname());
//        memberRepository.deleteAll();
//        after();
//    }
//
//
//    @Test
//    @DisplayName("회원가입시 토큰이 온다")
//    @Transactional
//    void 회원가입요청시토큰() throws Exception {
//
//        mockMvc.perform(post("/api/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
//                .andExpect(status().isOk())
//                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
//                .andExpect(cookie().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
//                .andExpect(jsonPath("email").exists())
//                .andExpect(jsonPath("profileImageUrl").exists())
//                .andExpect(jsonPath("nickname").exists());
//        after();
//
//    }
//
//    @Test
//    void signRequesetDto가잘못된경우회원가입() throws Exception {
//        mockMvc.perform(post("/api/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(badSignupReqestDto)))
//                .andExpect(status().isBadRequest());
//        Assertions.assertTrue(memberRepository.findByEmail(badSignupReqestDto.getEmail()).isEmpty());
//    }
//
//
//
//    @Test
//    @Transactional
//    void 이메일중복체크true() throws Exception {
//
//        //given
//        mockMvc.perform(post("/api/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(post("/api/user/check/Email")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(email))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("message").value("true"));
//        after();
//    }
//
//    @Test
//    void 이메일중복체크false() throws Exception {
//
//        mockMvc.perform(post("/api/user/check/Email")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(email))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("message").value("false"));
//        after();
//
//    }
//
//    @Autowired
//    MemberUpdateService memberUpdateService;
//
//    @Test
//    @Transactional
//    void 멤버삭제시에러없나() throws Exception {
//        memberRepository.save(new Member(normalSignupReqestDto, Role.ROLE_USER));
//        em.flush();
//        em.clear();
//
//        Assertions.assertDoesNotThrow(() -> memberRepository.deleteByEmail(normalSignupReqestDto.getEmail()));
//        Assertions.assertThrows(NoSuchElementException.class, () -> {
//            memberRepository.findByEmail(normalSignupReqestDto.getEmail()).get();
//        });
//    }
//
//    @Test
//    @Transactional
//    void deletemember메서드테스트() throws Exception {
//        Member member = memberRepository.save(new Member(normalSignupReqestDto, Role.ROLE_USER));
//        memberUpdateService.deleteMember(new MemberContext(member));
//
//        Assertions.assertThrows(NoSuchElementException.class, () -> {
//            memberRepository.findByEmail(normalSignupReqestDto.getEmail()).get();
//        });
//    }
//
//    @Test
//    @Transactional
//    void deleteMembermvcxptmxm() throws Exception {
//        String token = mockMvc.perform(post("/api/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(normalSignupReqestDto))).andReturn().getResponse().getHeader(JwtConfig.AUTHENTICATION_HEADER_NAME);
//
//
//        mockMvc.perform(post("/auth/api/user/inactivate")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(JwtConfig.AUTHENTICATION_HEADER_NAME,JwtConfig.TOKEN_TYPE+ token))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        Assertions.assertThrows(NoSuchElementException.class, () -> {
//            memberRepository.findByEmail(normalSignupReqestDto.getEmail()).get();
//        });
//    }
//
//
//}
