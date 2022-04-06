//package com.hanghae99.boilerplate.unitTest.hojun;
//
//import com.hanghae99.boilerplate.board.domain.Board;
//import com.hanghae99.boilerplate.board.domain.BoardRepository;
//import com.hanghae99.boilerplate.board.dto.BoardRequestDto;
//import com.hanghae99.boilerplate.board.service.BoardService;
//import com.hanghae99.boilerplate.board.service.BoardServiceImpl;
//import com.hanghae99.boilerplate.memberManager.model.Member;
//import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
//import com.hanghae99.boilerplate.security.model.MemberContext;
//import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
//import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.fail;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class HojunBoardTest {
//
//    @Autowired
//    BoardRepository boardRepository;
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    SignupLoginService signupLoginService ;
//    @Autowired
//    BoardServiceImpl boardService;
//
//
//    SignupReqestDto noramlSignupRequest = new SignupReqestDto("wns674211@naver.com", "1234", "nickname", "password");
//    BoardRequestDto boardRequestDto = new BoardRequestDto("title", "content", "image", "category");
//    MemberContext memberContext;
//
//
//    @BeforeEach
//    @Transactional
//    void 초기설정() {
//        memberRepository.deleteByEmail(noramlSignupRequest.getEmail());
//        memberContext =  signupLoginService.signupRequest(noramlSignupRequest);
//        System.out.println("==================================================");
//    }
//
//    //connection을 절약하자 !
//    //role에서도 connection을 절약하는게 가능할듯 ??
//
//    @Test
//    @Order(0)
//    void createBoard() throws Exception {
//    Assertions.assertDoesNotThrow(()-> boardService.createBoard(boardRequestDto,memberContext) );
//        System.out.println("==================================================");
//    }
//
//
//}
//
//
//
