package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dita.domain.MemberSub;

@Repository
public interface MemberSubRepository extends JpaRepository<MemberSub, String> {
    
	boolean existsBySubUserAndSubedUser(String subUser, String subedUser);
    void deleteBySubUserAndSubedUser(String subUser, String subedUser);
}
