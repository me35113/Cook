package com.dita.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Getter
@Setter
@Table(name = "member")
public class Member {

    @Id
    @NotBlank(message = "아이디는 필수입니다.")
    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @Column(name = "pwd", length = 60, nullable = false)
    private String pwd;

    @NotBlank(message = "이름은 필수입니다.")
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    @Column(name = "email", length = 200, nullable = false)
    private String email;

    @Column(name = "profile", length = 100)
    private String profile;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "zipcode", length = 255)
    private String zipcode;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "job", length = 255)
    private String job;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime memDate;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;

    @Column(name = "allergy", length = 255)
    private String allergy;

    @Column(name = "`rank`", length = 255)
    private String rank;

    // 새로 추가하는 필드 - 구독 여부
    @Transient  // DB 컬럼이 없고, 임시 데이터로 쓰고 싶으면 @Transient 붙임
    private Boolean subscribed;

    // 만약 DB에 컬럼이 존재한다면 @Column으로 매핑하면 됩니다.
    // 예: @Column(name = "subscribed") private Boolean subscribed;
}
