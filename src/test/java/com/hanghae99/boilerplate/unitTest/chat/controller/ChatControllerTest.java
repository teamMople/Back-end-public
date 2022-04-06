package com.hanghae99.boilerplate.unitTest.chat.controller;

import com.hanghae99.boilerplate.chat.controller.ChatController;
import com.hanghae99.boilerplate.chat.model.ChatVote;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.chat.service.ChatMessageService;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static com.hanghae99.boilerplate.chat.model.ChatVote.VoteType.AGREE;
import static com.hanghae99.boilerplate.chat.model.ChatVote.VoteType.DISAGREE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatControllerTest {

    private ChatController chatController;
    private RedisTemplate redisTemplate;
    private ChannelTopic channelTopic;
    private ChatMessageService chatMessageService;
    private RedisChatRoomRepository redisChatRoomRepository;
    private MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        this.redisTemplate = mock(RedisTemplate.class);
        this.channelTopic = mock(ChannelTopic.class);
        this.chatMessageService = mock(ChatMessageService.class);
        this.redisChatRoomRepository = mock(RedisChatRoomRepository.class);
        this.memberRepository = mock(MemberRepository.class);
        this.chatController = new ChatController(redisTemplate, channelTopic, chatMessageService, memberRepository, redisChatRoomRepository);
    }


    @Test
    @DisplayName("반대했던 사용자가 찬성시, subDisagree")
    void agreeFromWhoDisagreedBefore() throws Exception {
        //given
        ChatVote chatVote = new ChatVote();
        chatVote.setRoomId(7L);
        chatVote.setType(AGREE);
        chatVote.setDisagreedBefore(true);

        //when
        Long subtracted = 5L;
        Long notChanged = 6L;

        when(redisChatRoomRepository.addAgree("7")).thenReturn(10L);
        when(redisChatRoomRepository.subDisagree("7")).thenReturn(subtracted);
        when(redisChatRoomRepository.reportDisagreeCount("7")).thenReturn(notChanged);

        //then
        ChatVote vote = chatController.vote(chatVote);
        assertEquals(subtracted, vote.getDisagreeCount());
    }

    @Test
    @DisplayName("반대하지않았던 사용자가 찬성시, 그저 report")
    void agreeFromWhoDidntDisagreedBefore() throws Exception {
        //given
        ChatVote chatVote = new ChatVote();
        chatVote.setRoomId(7L);
        chatVote.setType(AGREE);
        chatVote.setDisagreedBefore(false);

        //when
        Long subtracted = 5L;
        Long notChanged = 6L;

        when(redisChatRoomRepository.addAgree("7")).thenReturn(10L);
        when(redisChatRoomRepository.subDisagree("7")).thenReturn(subtracted);
        when(redisChatRoomRepository.reportDisagreeCount("7")).thenReturn(notChanged);

        //then
        ChatVote vote = chatController.vote(chatVote);
        assertEquals(notChanged, vote.getDisagreeCount());
    }

    @Test
    @DisplayName("찬성했던 사용자가 반대시, subAgree")
    void disagreeFromWhoAgreedBefore() throws Exception {
        //given
        ChatVote chatVote = new ChatVote();
        chatVote.setRoomId(7L);
        chatVote.setType(DISAGREE);
        chatVote.setAgreedBefore(true);

        //when
        Long subtracted = 5L;
        Long notChanged = 6L;

        when(redisChatRoomRepository.addDisagree("7")).thenReturn(10L);
        when(redisChatRoomRepository.subAgree("7")).thenReturn(subtracted);
        when(redisChatRoomRepository.reportAgreeCount("7")).thenReturn(notChanged);

        //then
        ChatVote vote = chatController.vote(chatVote);
        assertEquals(subtracted, vote.getAgreeCount());
    }

    @Test
    @DisplayName("찬성하지 않았던 사용자가 반대시, 그저 report")
    void disagreeFromWhoDidntAgreedBefore() throws Exception {
        //given
        ChatVote chatVote = new ChatVote();
        chatVote.setRoomId(7L);
        chatVote.setType(DISAGREE);
        chatVote.setAgreedBefore(false);

        //when
        Long subtracted = 5L;
        Long notChanged = 6L;

        when(redisChatRoomRepository.addDisagree("7")).thenReturn(10L);
        when(redisChatRoomRepository.subAgree("7")).thenReturn(subtracted);
        when(redisChatRoomRepository.reportAgreeCount("7")).thenReturn(notChanged);

        //then
        ChatVote vote = chatController.vote(chatVote);
        assertEquals(notChanged, vote.getAgreeCount());
    }

}
