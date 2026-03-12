package com.portfolio.fastbreak.domain.member.repository;

import com.portfolio.fastbreak.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 이메일 중복 여부 확인
    boolean existsByEmail(String email);
}
