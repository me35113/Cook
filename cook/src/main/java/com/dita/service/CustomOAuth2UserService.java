package com.dita.service;

import com.dita.domain.Member;
import com.dita.persistence.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final HttpServletRequest request; // 세션 사용을 위한 요청 객체

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "kakao" or "naver"
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String name = null;
        String providerId = null;
        Map<String, Object> userAttributes;

        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            name = (String) response.get("name");
            providerId = (String) response.get("id");
            userAttributes = response;

        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            providerId = attributes.get("id").toString();
            name = (String) profile.get("nickname");
            email = (String) kakaoAccount.get("email");

            if (email == null) {
                email = "kakao_" + providerId + "@socialuser.com";
            }

            // 세션에 accessToken과 registrationId 저장
            HttpSession session = request.getSession();
            session.setAttribute("registrationId", registrationId);
            session.setAttribute("kakaoAccessToken", userRequest.getAccessToken().getTokenValue());

            // email 필드 강제 추가
            Map<String, Object> updatedAttributes = new HashMap<>(attributes);
            updatedAttributes.put("email", email);
            userAttributes = updatedAttributes;

        } else {
            providerId = attributes.get("id").toString();
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            userAttributes = attributes;
        }

        final String finalEmail = email;
        final String finalName = name != null ? name : registrationId + "_" + providerId;

        // 사용자 정보 저장 또는 조회
        Member member = memberRepository.findByEmail(finalEmail)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setUserId(finalEmail);
                    newMember.setEmail(finalEmail);
                    newMember.setName(finalName);
                    newMember.setPwd(UUID.randomUUID().toString());
                    return memberRepository.save(newMember);
                });

        // 로그인 ID를 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("loginId", finalEmail);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                userAttributes,
                "email"
        );
    }
}
