package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.annotation.TimeTrace;
import com.hanghae99.boilerplate.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class ChatMessageJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

//    @Value("${batchSize}")
//    private int batchSize;
    private int batchSize = 300;

    @TimeTrace
    public void saveAll(List<ChatMessage> messages) {
        int batchCount = 0;
        List<ChatMessage> subMessages = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            subMessages.add(messages.get(i));
            if ((i + 1) % batchSize == 0) {
                batchCount = batchInsert(batchSize, batchCount, subMessages);
            }
        }
        if (!subMessages.isEmpty()) {
            batchCount = batchInsert(batchSize, batchCount, subMessages);
        }
        System.out.println("batchCount: " + batchCount);
    }

    private int batchInsert(int batchSize, int batchCount, List<ChatMessage> subMessages) {
        jdbcTemplate.batchUpdate("INSERT INTO chat_message (`room_id`, `sender`, `message`, `sent_at`) VALUES (?, ?, ?, ?)",

                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, subMessages.get(i).getRoomId());
                        ps.setString(2, subMessages.get(i).getSender());
                        ps.setString(3, subMessages.get(i).getMessage());
                        ps.setString(4, subMessages.get(i).getSentAt());
                    }

                    @Override
                    public int getBatchSize() {
                        return subMessages.size();
                    }
                });
        subMessages.clear();
        batchCount++;
        return batchCount;
    }
}