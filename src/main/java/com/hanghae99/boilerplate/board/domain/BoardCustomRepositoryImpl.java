package com.hanghae99.boilerplate.board.domain;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.hanghae99.boilerplate.board.domain.QBoard.board;

@Repository
@Slf4j
public class BoardCustomRepositoryImpl extends QuerydslRepositorySupport implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public BoardCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        super(Board.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public List<Board> findByIdJoinFetch(Long boardId) {
        return jpaQueryFactory.selectFrom(board)
                .where(board.id.eq(boardId))
                .innerJoin(board.comments)
                .fetchJoin()
                .fetch();
    }

    @Override //추가
    public PageImpl<Board> findTopVotedBoards(Pageable pageable) {

        JPAQuery<Board> query = jpaQueryFactory
                .selectFrom(board)
                .where(board.isDeleted.eq(false))
                .orderBy(board.agreeCount.add(board.disagreeCount).desc());

        long totalCount = query.fetchCount();
        List<Board> results = getQuerydsl().applyPagination(pageable, query).fetch();
        int pageSize = pageable.getPageSize();
        log.info("페이징 확인 pageSize: {}", pageSize);
        log.info("페이징 확인 results Size: {}", results.size());

        return new PageImpl<>(results, pageable, totalCount);

//        return jpaQueryFactory
//                .selectFrom(board)
//                .orderBy(board.agreeCount.add(board.disagreeCount).desc())
//                .offset(offset)
//                .limit(size)
//                .fetch();
    }
}
