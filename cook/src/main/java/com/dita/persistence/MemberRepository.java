package com.dita.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Member;
import java.util.List;


public interface MemberRepository extends JpaRepository<Member, String> {
  Optional<Member> findByUserId(String userId);
}
