package com.dita.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.dita.domain.Member;
import com.dita.persistence.MemberRepository;
import com.dita.persistence.MemberService;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
