package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dita.domain.MemberSub;
import com.dita.domain.MemberSubId;

import jakarta.transaction.Transactional;

@Repository
public interface MemberSubRepository extends JpaRepository<MemberSub, MemberSubId> {
   
    void deleteBySubUserAndSubedUser(String subUser, String subedUser);
    
    MemberSub findBySubUserAndSubedUser(String subUser, String subedUser);
    
    // 내가 구독한 수
    int countBySubUserAndState(String subUser, int state);

    // 나를 구독한 수
    int countBySubedUserAndState(String subedUser, int state);

    // 구독 상태로 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE MemberSub m SET m.state = 1 WHERE m.subUser = :subUser AND m.subedUser = :subedUser")
    void subscribe(@Param("subUser") String subUser, @Param("subedUser") String subedUser);

    // 구독 상태 취소
    @Modifying
    @Transactional
    @Query("UPDATE MemberSub m SET m.state = 0 WHERE m.subUser = :subUser AND m.subedUser = :subedUser")
    void unsubscribe(@Param("subUser") String subUser, @Param("subedUser") String subedUser);
    
    boolean existsBySubUserAndSubedUserAndState(String subUser, String subedUser, Integer state);
}
