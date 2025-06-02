package com.dita.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> { // 기본키가 Long이라 가정

    Optional<Member> findByEmail(String email);

    boolean existsByUserId(String userId);
}
