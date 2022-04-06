package com.hanghae99.boilerplate.board.domain;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardCustomRepository {

    List<Board> findByIdJoinFetch(Long boardId);

    PageImpl<Board> findTopVotedBoards(Pageable pageable);
}
