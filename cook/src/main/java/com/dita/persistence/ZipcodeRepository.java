package com.dita.persistence;

import com.dita.domain.Zipcode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZipcodeRepository extends JpaRepository<Zipcode, String> {

    Optional<Zipcode> findByZipcode(String zipcode);
}
