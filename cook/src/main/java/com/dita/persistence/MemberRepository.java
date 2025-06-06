package com.dita.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dita.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByEmail(String email);

    boolean existsByUserId(String userId);

    // ① 전체 Member 조회
    Optional<Member> findByZipcode(String zipcode);
    
    Optional<Member> findByUserId(String userId);

    // ② 주소만 문자열로 조회
    @Query("SELECT m.address FROM Member m WHERE m.zipcode = :zipcode")
    Optional<String> findAddressByZipcode(@Param("zipcode") String zipcode);
}
