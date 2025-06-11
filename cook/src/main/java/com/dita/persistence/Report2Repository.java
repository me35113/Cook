package com.dita.persistence;

import com.dita.domain.Report2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Report2Repository extends JpaRepository<Report2, Long> {
    // 필요하다면 커스텀 쿼리 메소드 추가 가능
}
