package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.dto.ChatLeaveDto;
import com.hanghae99.boilerplate.chat.dto.ChatMessageDto;
import com.hanghae99.boilerplate.chat.model.ChatRole;
import com.hanghae99.boilerplate.chat.model.ChatVote;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.chat.service.ChatMessageService;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Controller
@CrossOrigin
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;


    // 찬성과 반대가 room의 정보를 변화시켜야 하므로, repository 불러오기 / 그런데 누구를? redis를 하고 싶다.
    private final RedisChatRoomRepository redisChatRoomRepository;

    @MessageMapping("/chat/vote")
//    /pub/chat/vote
    public ChatVote vote(ChatVote chatVote) {
        String roomId = chatVote.getRoomId().toString();
        Boolean agreedBefore = chatVote.getAgreedBefore();
        Boolean disagreedBefore = chatVote.getDisagreedBefore();

        switch (chatVote.getType()) {

            case AGREE:
                log.info("AGREE: {}", chatVote.getSender());
                Long afterAgree = redisChatRoomRepository.addAgree(roomId);
                chatVote.setAgreeCount(afterAgree);

                if (disagreedBefore) {
                    Long afterCancelDisagree = redisChatRoomRepository.subDisagree(roomId);
                    chatVote.setDisagreeCount(afterCancelDisagree);
                } else {
                    chatVote.setDisagreeCount(redisChatRoomRepository.reportDisagreeCount(roomId));
                }
                break;

            case CANCEL_AGREE:
                log.info("CANCEL_AGREE: {}", chatVote.getSender());
                Long afterCancelAgree = redisChatRoomRepository.subAgree(roomId);
                chatVote.setAgreeCount(afterCancelAgree);
                chatVote.setDisagreeCount(redisChatRoomRepository.reportDisagreeCount(roomId));
                break;

            case DISAGREE:
                log.info("DISAGREE: {}", chatVote.getSender());
                Long afterDisagree = redisChatRoomRepository.addDisagree(roomId);
                chatVote.setDisagreeCount(afterDisagree);

                if (agreedBefore) {
                    Long afterCancelAgreeCount = redisChatRoomRepository.subAgree(roomId);
                    chatVote.setAgreeCount(afterCancelAgreeCount);
                } else {
                    chatVote.setAgreeCount(redisChatRoomRepository.reportAgreeCount(roomId));
                }
                break;

            case CANCEL_DISAGREE:
                log.info("CANCEL_DISAGREE: {}", chatVote.getSender());
                Long afterCancelDisagree = redisChatRoomRepository.subDisagree(roomId);
                chatVote.setDisagreeCount(afterCancelDisagree);
                chatVote.setAgreeCount(redisChatRoomRepository.reportAgreeCount(roomId));
                break;
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatVote);
        return chatVote;
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto chatMessageDto) {

        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageDto);

        if (chatMessageDto.getType().equals(ChatMessageDto.MessageType.LEAVE)) {
            Long roomId = chatMessageDto.getRoomId();
            String memberName = chatMessageDto.getSender();
            ChatRole role = chatMessageDto.getRole();
            Boolean agreed = chatMessageDto.getAgreed();
            Boolean disagreed = chatMessageDto.getDisagreed();

            ChatLeaveDto chatLeaveDto = new ChatLeaveDto();
            chatLeaveDto.setRoomId(roomId);
            chatLeaveDto.setMemberName(memberName);
            chatLeaveDto.setRole(role);
            chatLeaveDto.setAgreed(agreed);
            chatLeaveDto.setDisagreed(disagreed);

            Optional<Member> byNickname = memberRepository.findByNickname(memberName);
            Member member = byNickname.get();

            redisChatRoomRepository.subParticipant(roomId.toString(), member, chatLeaveDto);
            log.info("leave 메시지를 받았고, redis에서 퇴장처리도 완료!, memberName: {}", memberName);

        }
        chatMessageService.tempStack(chatMessageDto);
    }

}