package com.dita.domain;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "아이디는 필수 항목입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String pwd;

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    private String profile;

    @NotBlank(message = "성별을 선택해주세요.")
    private String gender;

    private String zipcode;
    private String address;
    private String job;
    private String intro;
    private String allergy;

    @Min(value = 0, message = "등급은 0 이상이어야 합니다.")
    private int grade;

    private String rank;
}
