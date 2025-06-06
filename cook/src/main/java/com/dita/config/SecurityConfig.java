package com.dita.config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.dita.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 여기에 /addressSearchPopup 추가
                .requestMatchers("/", "/login", "/signup", "/checkUserId","/boardlist_read", 
                		"/searchAddress", "/addressSearchPopup","/boardlist_write","/boardlist","/registration","/category",
                		"/css/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .defaultSuccessUrl("/home", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(customLogoutHandler())
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)  // 세션 무효화
                .deleteCookies("JSESSIONID")  // 쿠키 삭제
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public LogoutHandler customLogoutHandler() {
        return (request, response, authentication) -> {
            System.out.println(">>> 로그아웃 핸들러 호출됨, authentication: " + authentication);

            if (authentication != null && authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String registrationId = oauthToken.getAuthorizedClientRegistrationId();

                var client = authorizedClientService.loadAuthorizedClient(registrationId, oauthToken.getName());

                if (client != null && client.getAccessToken() != null) {
                    String accessToken = client.getAccessToken().getTokenValue();

                    try {
                        if ("kakao".equals(registrationId)) {
                            kakaoLogout(accessToken);
                        } else if ("naver".equals(registrationId)) {
                            naverLogout(accessToken);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // OAuth2AuthorizedClient 저장소에서 해당 클라이언트 제거
                    authorizedClientService.removeAuthorizedClient(registrationId, oauthToken.getName());
                    System.out.println(">>> authorizedClientService에서 클라이언트 제거 완료");
                }
            }
        };
    }

    private void kakaoLogout(String accessToken) throws IOException {
        URL url = new URL("https://kapi.kakao.com/v1/user/logout");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setDoOutput(true);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.err.println("카카오 로그아웃 실패. 응답 코드: " + responseCode);
        } else {
            System.out.println("카카오 로그아웃 성공");
        }
        conn.disconnect();
    }

    private void naverLogout(String accessToken) throws IOException {
        String clientId = "GWT46vjYfA0zmf1THiI0";
        String clientSecret = "V9A5VDmgrj";

        String urlStr = String.format("https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=%s&client_secret=%s&access_token=%s&service_provider=NAVER",
                clientId, clientSecret, accessToken);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.err.println("네이버 로그아웃(토큰 삭제) 실패. 응답 코드: " + responseCode);
        } else {
            System.out.println("네이버 로그아웃 성공");
        }
        conn.disconnect();
    }
}
