package com.hanghae99.boilerplate.board.domain;

import com.hanghae99.boilerplate.memberManager.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository{
    Page<Board> findAll(Pageable pageable);
    List<Board> findAllByIsDeletedIsFalseOrderByCreatedAtDesc(Pageable pageable);

    List<Board> findAllByCategoryAndIsDeletedIsFalse(String categoryName);

    List<Board> findByTitleContains(String name);
    List<Board> findAllByMemberAndIsDeletedIsFalse(Member user);

    @Query(value = "select * from board b where b.is_deleted=FALSE ORDER BY agree_count + disagree_count desc LIMIT 5 ", nativeQuery = true)
    List<Board> findTop5ByOrderByAgreeCountAndDisagreeCountDesc();

//    @Query("select DISTINCT b from Board b join fetch b.comments")
//    Board findByIdJoinFetch(Long boardId);
}
