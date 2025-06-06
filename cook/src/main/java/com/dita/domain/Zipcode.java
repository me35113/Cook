package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tblzipcode")
public class Zipcode {

    @Id
    @Column(length = 10, nullable = false)
    private String zipcode;

    @Column(length = 10)
    private String area1;

    @Column(length = 20)
    private String area2;

    @Column(length = 30)
    private String area3;

    @Column(length = 255)
    private String address;

    // 추가로 주소 전체를 조합하는 메서드 (선택사항)
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (area1 != null) sb.append(area1.trim()).append(" ");
        if (area2 != null) sb.append(area2.trim()).append(" ");
        if (area3 != null) sb.append(area3.trim());
        return sb.toString().trim();
    }
}
