package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.annotation.TimeTrace;
import com.hanghae99.boilerplate.chat.dto.*;
import com.hanghae99.boilerplate.chat.model.ChatRole;
import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.chat.util.DateTimeComparator;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MemberRepository memberRepository;
    private final DateTimeComparator comparator;

    //    ************************* 채팅방 (생성, 입장, 퇴장, 종료)  **************************
    // 채팅방 생성 ( db 에 생성, ->  redis )
    @Override
    @Transactional
    @TimeTrace
    public ChatRoomCreateResDto createChatRoom(CreateChatRoomDto createChatRoomDto, MemberContext user) {
        Optional<Member> optionalMember = memberRepository.findById(user.getMemberId());
        validateMember(optionalMember);
        Member member = optionalMember.get();

        ChatRoom room = new ChatRoom(createChatRoomDto, member);
        //db
        chatRoomRepository.save(room);
        //redis
        ChatRoomRedisDto chatRoomRedisDto = redisChatRoomRepository.createChatRoom(room.getRoomId().toString(), room);
        ChatRoomCreateResDto chatRoomCreateResDto = new ChatRoomCreateResDto(chatRoomRedisDto);
        chatRoomCreateResDto.setMemberName(createChatRoomDto.getModerator());
        chatRoomCreateResDto.setRole(ChatRole.MODERATOR);
        return chatRoomCreateResDto;
    }


    // 채팅방 입장 ( redis )
    @Override
    @TimeTrace
    public ChatRoomEntryResDto addParticipant(ChatEntryDto entryDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);
        Member member = findMember.get();
        Long maxParticipantCount = entryDto.getParticipantCount();

        ChatRoomEntryResDto entryResDto = redisChatRoomRepository.addParticipant(entryDto.getRoomId().toString(), member, maxParticipantCount);
        entryResDto.setMemberName(entryDto.getMemberName());
        entryResDto.setRole(entryDto.getRole());
        return entryResDto;
    }

    // 채팅방 퇴장 ( redis )
    @Override
    @TimeTrace
    public ChatRoomRedisDto leaveParticipant(ChatLeaveDto leaveDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);

        log.info("퇴장하려는 사람의 nickname: {}, role: {}", findMember.get().getNickname(), leaveDto.getRole());
        return redisChatRoomRepository.subParticipant(leaveDto.getRoomId().toString(), findMember.get(), leaveDto);
    }

    // 채팅방 종료 ( redis , -> db update )
    @Override
//    @Transactional
    @TimeTrace
    public String closeRoom(ChatCloseDto chatCloseDto, @AuthenticationPrincipal MemberContext user) {
//
        Optional<Long> roomId = Optional.ofNullable(chatCloseDto.getRoomId());

        // redis 에서 삭제
        if (roomId.isPresent() && redisChatRoomRepository.findChatRoomRedisDtoById(roomId.toString()) != null) {
            redisChatRoomRepository.removeRoom(roomId.toString());
        }

        return roomId + "가 성공적으로 종료되었습니다.";
    }


//    ************************* 라이브 채팅방 조회 (from redis) **************************

    // 라이브 채팅방 조회 : 전체  ( redis )
    @Override
    @TimeTrace
    public List<ChatRoomRedisDto> findOnAirChatRooms() {
        List<ChatRoomRedisDto> allRoomsOnAir = redisChatRoomRepository.findAllRoom();
        Collections.sort(allRoomsOnAir, comparator);
        return allRoomsOnAir;
    }

    // 라이브 채팅방 조회 : 카테고리  ( redis )
    @Override
    @TimeTrace
    public List<ChatRoomRedisDto> findOnAirChatRoomsByCategory(String category) {
        List<ChatRoomRedisDto> chatRoomRedisDtos = redisChatRoomRepository.findByCategory(category);
        Collections.sort(chatRoomRedisDtos, comparator);
        return chatRoomRedisDtos;
    }

    // 라이브 채팅방 조회 : 키워드  ( redis )
    @Override
    @TimeTrace
    public List<ChatRoomRedisDto> findOnAirChatRoomsByKeyword(String keyword) {
        List<ChatRoomRedisDto> chatRoomRedisDtos = redisChatRoomRepository.findByKeyword(keyword);
        Collections.sort(chatRoomRedisDtos, comparator);
        return chatRoomRedisDtos;
    }


//    ************************* 검증용 보조 method  **************************

    private void validateMember(Optional<Member> findMember) {
        if (findMember == null) {
            throw new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다.");
        }
    }

    private void validateChatRoom(Optional<ChatRoom> optionalChatRoom) {
        if (!optionalChatRoom.isPresent()) {
            throw new IllegalArgumentException("해당 Id의 방이 없습니다.");
        }
    }
}
