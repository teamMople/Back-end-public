package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.dto.ChatMessageDto;
import com.hanghae99.boilerplate.chat.model.ChatMessage;
import com.hanghae99.boilerplate.chat.repository.ChatMessageJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
    private final ChatMessageJdbcRepository chatMessageRepository;

    // 방별로 채팅메시지 모아놓고
    // 개수가 100개쯤? (일단 batchSize=300으로 하긴 함) 되는 시점에 batch insert query 를 넣어보면 어떨까?
    // 100개 조금 많은 것 같아서, 저장이 되는지 보려고 20개로 줄여봄

    public void tempStack(ChatMessageDto chatMessageDto) {
        this.chatMessageDtos.add(chatMessageDto);

        if (this.chatMessageDtos.size() >= 20) {
            List<ChatMessage> chatMessages = chatMessageDtos.stream().map(dto -> new ChatMessage(dto)).collect(Collectors.toList());
            chatMessageRepository.saveAll(chatMessages);
            chatMessageDtos.clear();
        }

    }



}
