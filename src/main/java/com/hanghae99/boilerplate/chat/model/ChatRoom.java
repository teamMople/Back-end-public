package com.hanghae99.boilerplate.chat.model;

import com.hanghae99.boilerplate.chat.dto.CreateChatRoomDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String roomName;
    private String category;

    @ManyToOne
    private Member moderator;
    private Long maxParticipantCount;
    @Column(length = 300)
    private String content;

//
//    @BatchSize(size = 500)
//    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.PERSIST})
//    private List<ChatEntry> entries = new ArrayList<>();

    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;
    private LocalDateTime closedAt; 
    private Boolean onAir = true;

    public ChatRoom(CreateChatRoomDto dto, Member member) {
        this.roomName = dto.getRoomName();
        this.category = dto.getCategory();
        this.maxParticipantCount = dto.getMaxParticipantCount();
        this.content = dto.getContent();
        this.moderator = member;
    }

    public ChatRoom closeChatRoom(Long agreeCount, Long disagreeCount, LocalDateTime closedAt) {
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
        this.closedAt = closedAt;
        this.onAir = false;
//        this.entries = entries;
        return this;
    }

}