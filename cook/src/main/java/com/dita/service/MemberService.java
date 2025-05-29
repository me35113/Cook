package com.dita.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dita.domain.Member;
import com.dita.domain.SignupRequestDto;
import com.dita.persistence.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 아이디 중복 체크
    public boolean existsByUserId(String userId) {
        return memberRepository.existsById(userId);
    }

    // 회원 저장
    public void save(SignupRequestDto dto) {
        Member member = new Member();

        member.setUserId(dto.getUserId());
        member.setPwd(dto.getPwd()); // 암호화 없이 평문 저장 중
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setProfile(dto.getProfile());
        member.setGender(dto.getGender());
        member.setZipcode(dto.getZipcode());
        member.setAddress(dto.getAddress());
        member.setJob(dto.getJob());
        member.setIntro(dto.getIntro());
        member.setAllergy(dto.getAllergy());
        member.setGrade(dto.getGrade());
        member.setRank(dto.getRank());

        memberRepository.save(member);
    }
    
    public Member findByUserId(String userId) {
        return memberRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("해당 아이디가 존재하지 않습니다."));
    }
}
