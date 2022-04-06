package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

//@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Modifying
    @Query("UPDATE ChatRoom c SET c.closedAt = :closedAt, c.agreeCount = :agreeCount, c.disagreeCount = :disagreeCount, c.onAir = false WHERE c.roomId = :roomId")
    int updateRoomWhenClosing(@Param("agreeCount")Long agreeCount, @Param("disagreeCount")Long disagreeCount, @Param("closedAt")LocalDateTime closedAt, @Param("roomId")Long roomId);


}