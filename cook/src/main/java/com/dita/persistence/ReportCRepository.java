package com.dita.persistence;

import com.dita.domain.ReportC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportCRepository extends JpaRepository<ReportC, Integer> {
    // 기본적인 findAll(), save(), deleteById() 등 제공
}
