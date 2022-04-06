package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.model.ChatEntry;
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
public class ChatEntryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

//    @Value("${batchSize}")
//    private int batchSize;
    private int batchSize = 100;

    public void saveAll(List<ChatEntry> entries) {
        int batchCount = 0;
        List<ChatEntry> subEntries = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            subEntries.add(entries.get(i));
            if ((i + 1) % batchSize == 0) {
                batchCount = batchInsert(batchSize, batchCount, subEntries);
            }
        }
        if (!subEntries.isEmpty()) {
            batchCount = batchInsert(batchSize, batchCount, subEntries);
        }
        System.out.println("batchCount: " + batchCount);
    }

    private int batchInsert(int batchSize, int batchCount, List<ChatEntry> subEntries) {
        jdbcTemplate.batchUpdate("INSERT INTO chat_entry (`member_id`, `room_id`) VALUES (?, ?)",

                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, subEntries.get(i).getMemberId());
                        ps.setLong(2, subEntries.get(i).getRoomId());
                    }

                    @Override
                    public int getBatchSize() {
                        return subEntries.size();
                    }
                });
        subEntries.clear();
        batchCount++;
        return batchCount;
    }
}