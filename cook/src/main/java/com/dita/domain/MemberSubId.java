// MemberSubId.java
package com.dita.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MemberSubId implements Serializable {

    @Column(name = "sub_user")
    private String subUser;

    @Column(name = "subed_user")
    private String subedUser;

    // 기본 생성자
    public MemberSubId() {}

    public MemberSubId(String subUser, String subedUser) {
        this.subUser = subUser;
        this.subedUser = subedUser;
    }

    // equals & hashCode 구현 필수
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberSubId)) return false;
        MemberSubId that = (MemberSubId) o;
        return Objects.equals(subUser, that.subUser) &&
               Objects.equals(subedUser, that.subedUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subUser, subedUser);
    }

}
