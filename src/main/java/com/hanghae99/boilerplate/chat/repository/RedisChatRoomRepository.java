package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.annotation.TimeTrace;
import com.hanghae99.boilerplate.chat.dto.ChatLeaveDto;
import com.hanghae99.boilerplate.chat.dto.ChatRoomEntryResDto;
import com.hanghae99.boilerplate.chat.dto.ChatRoomRedisDto;
import com.hanghae99.boilerplate.chat.model.ChatEntry;
import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisChatRoomRepository {
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM_REDIS_DTOS";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomRedisDto> opsHashChatRoom;

    //db의존
    private final ChatRoomRepository chatRoomRepository;
    private final ChatEntryJdbcRepository chatEntryJdbcRepository;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    //채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash 에 저장
    @TimeTrace
    public ChatRoomRedisDto createChatRoom( String roomId, ChatRoom chatRoom) {
        ChatRoomRedisDto redisDto = new ChatRoomRedisDto(chatRoom);
        opsHashChatRoom.put(CHAT_ROOMS, roomId, redisDto);
        return redisDto;
    }

    //채팅방 입장
    @TimeTrace
    public ChatRoomEntryResDto addParticipant(String roomId, Member member, Long maxParticipantCount) {
        Optional<ChatRoomRedisDto> opitonalChatRoomRedisDto = Optional.ofNullable(opsHashChatRoom.get(CHAT_ROOMS, roomId));
        if (opitonalChatRoomRedisDto.isPresent()) {
            ChatRoomRedisDto chatRoomRedisDto = opitonalChatRoomRedisDto.get();

            int nowParticipantCount = chatRoomRedisDto.getParticipantsIds().size();
            if (maxParticipantCount <= nowParticipantCount) {
                throw new IllegalArgumentException("참여인원이 이미 찼어요!");
            }

            ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.addParticipant(member);
            opsHashChatRoom.put(CHAT_ROOMS, roomId, mChatRoomRedisDto);
            ChatRoomEntryResDto entryResDto = getChatRoomEntryResDto(mChatRoomRedisDto, member);
            return entryResDto;
        } else {
            Optional<ChatRoom> roomFromDb = chatRoomRepository.findById(Long.valueOf(roomId));
            if (roomFromDb.isPresent()) {
                if (roomFromDb.get().getOnAir() == true) {
                    ChatRoomRedisDto chatRoomRedisDto = new ChatRoomRedisDto(roomFromDb.get());
                    ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.addParticipant(member);
                    opsHashChatRoom.put(CHAT_ROOMS, roomId, chatRoomRedisDto);
                    ChatRoomEntryResDto entryResDto = getChatRoomEntryResDto(mChatRoomRedisDto, member);
                    return entryResDto;
                } else {
                    throw new IllegalArgumentException("해당 Id의 chatRoom이 종료되었습니다.");
                }
            } else {
                throw new IllegalArgumentException("해당 Id의 chatRoom이 개설되지 않았습니다.");
            }
        }
    }

    private ChatRoomEntryResDto getChatRoomEntryResDto(ChatRoomRedisDto mChatRoomRedisDto, Member member) {
        ChatRoomEntryResDto entryResDto = new ChatRoomEntryResDto(mChatRoomRedisDto);
        Boolean memberAgreed = (mChatRoomRedisDto.getAgreed().get(member.getId()) != null) ? mChatRoomRedisDto.getAgreed().get(member.getId()) : false;
        Boolean memberDisagreed = (mChatRoomRedisDto.getDisagreed().get(member.getId()) != null) ? mChatRoomRedisDto.getDisagreed().get(member.getId()) : false;

        entryResDto.setMemberAgreed(memberAgreed);
        entryResDto.setMemberDisagreed(memberDisagreed);
        return entryResDto;
    }

    //채팅방 퇴장
    @TimeTrace
    @Transactional
    public ChatRoomRedisDto subParticipant(String roomId, Member member, ChatLeaveDto leaveDto) {
        Optional<ChatRoomRedisDto> optionalChatRoomRedisDto = Optional.ofNullable(opsHashChatRoom.get(CHAT_ROOMS, roomId));
        if (!optionalChatRoomRedisDto.isPresent()) {
            throw new IllegalArgumentException("이미 종료되었거나 존재하지 않는 방입니다.");
        }
        ChatRoomRedisDto chatRoomRedisDto = optionalChatRoomRedisDto.get();
        ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.subParticipant(member);

        if (mChatRoomRedisDto.getParticipantsIds().size() == 0) {

            chatRoomRepository.updateRoomWhenClosing(mChatRoomRedisDto.getAgreeCount(), mChatRoomRedisDto.getDisagreeCount(),
                    LocalDateTime.now(), Long.valueOf(roomId));

            List<ChatEntry> entries = this.reportTotalMaxParticipantsIds(roomId).stream()
                    .map(memberId -> {
                        return new ChatEntry(memberId, Long.valueOf(roomId));
                    }).collect(toList());

            chatEntryJdbcRepository.saveAll(entries);


            this.removeRoom(roomId);
            return mChatRoomRedisDto;
        }

        ChatRoomRedisDto nChatRoomRedisDto = mChatRoomRedisDto.recordMemberAgreedOrDisagreed(member, leaveDto);
        opsHashChatRoom.put(CHAT_ROOMS, roomId, nChatRoomRedisDto);
        return nChatRoomRedisDto;
    }

    //채팅방 제거
    public void removeRoom(String roomId) {
        Long delete = opsHashChatRoom.delete(CHAT_ROOMS, roomId);
    }

    // ***************************** 실시간 찬반투표 *******************************

    public Long addAgree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.addAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getAgreeCount();
        return after;
    }

    public Long subAgree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.subAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getAgreeCount();
        return after;
    }

    public Long addDisagree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.addDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getDisagreeCount();
        return after;
    }

    public Long subDisagree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.subDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getDisagreeCount();
        return after;
    }

    // ***************************** 채팅방 종료시 최종 기록 업데이트 *******************************

    public Long reportAgreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getAgreeCount();
    }

    public Long reportDisagreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getDisagreeCount();
    }

    public Set<Long> reportTotalMaxParticipantsIds(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getTotalMaxParticipantsIds();
    }

    ///// (+ 보조)
    public ChatRoomRedisDto findChatRoomRedisDtoById(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId);
    }

// ***************************** 조회 (라이브) *******************************

    // 전체 조회
    @TimeTrace
    public List<ChatRoomRedisDto> findAllRoom() {
        // 방식1. 빠르게. 0명남아서 사실상 죽은 방 검증은 없이
        List<ChatRoomRedisDto> redisDtos = opsHashChatRoom.values(CHAT_ROOMS);
        // 방식2. close 호출은 안됐지만, leave 호출은 돼서 참여자수 0명으로 되어있는 방은 거르기
        // 이거는, 종료처리가 잘 된다면 꼭 확인하지 않아도 될 절차인 것 같아! 일단 주석해놓자. 너무 시간 걸리는 것 같으니까.
//        List<ChatRoomRedisDto> filteredDtos = new ArrayList<>();
//
//        for (ChatRoomRedisDto redisDto : redisDtos) {
//            if (redisDto.getParticipantsIds().size() > 0) {
//                filteredDtos.add(redisDto);
//            }
//        }
//
//        log.info("🥸 filtered(0명인채로 남아버린 방은 가려라! filter된 방은 몇개? {}", filteredDtos.size());
//        log.info("🥸 filtered 되기 전 전체 방은 몇개? {}", redisDtos.size());

        return redisDtos;
    }

    // 카테고리로 조회 (만약 카테고리를 key 로 둔다면? 그럼 더 빠를 것 같은데..)
    @TimeTrace
    public List<ChatRoomRedisDto> findByCategory(String category) {
        List<ChatRoomRedisDto> resultDtos = new ArrayList<>();
        List<ChatRoomRedisDto> all = opsHashChatRoom.values(CHAT_ROOMS);
        for (ChatRoomRedisDto redisDto : all) {
            if (redisDto.getCategory() != null && redisDto.getCategory().equals(category)) {
                resultDtos.add(redisDto);
            }
        }
        return resultDtos;
    }

    // 키워드 조회
    @TimeTrace
    public List<ChatRoomRedisDto> findByKeyword(String keyword) {
        List<ChatRoomRedisDto> resultDtos = new ArrayList<>();

        List<ChatRoomRedisDto> all = opsHashChatRoom.values(CHAT_ROOMS);
        for (ChatRoomRedisDto redisDto : all) {
            if ((redisDto.getRoomName() != null) && (redisDto.getRoomName().contains(keyword))) {
                resultDtos.add(redisDto);
            }
        }
        return resultDtos;
    }

}
