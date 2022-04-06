package com.hanghae99.boilerplate.board.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTargetRepository extends JpaRepository<FcmTarget, Long> {


    Optional<FcmTarget> findByMemberId(Long memberId);
}
