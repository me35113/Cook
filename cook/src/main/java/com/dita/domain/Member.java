package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "member")
public class Member {

    @Id
    @Column(name = "user_id", length = 20, nullable = false)
    private String userId;

    @Column(name = "pwd", length = 30, nullable = false)
    private String pwd;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

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

    @Column(name = "mem_date")
    private LocalDateTime memDate;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;

    @Column(name = "allergy", length = 255)
    private String allergy;

    @Column(name = "`rank`", length = 255)
    private String rank;
}
