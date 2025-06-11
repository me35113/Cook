package com.dita.persistence;

import java.util.List;
import com.dita.domain.Member;

public interface MemberService {
    List<Member> getAllMembers();  // 회원 전체 목록 조회
}
