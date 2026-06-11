package com.noda.api.repositories;

import com.noda.api.models.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {
    Optional<OneTimePassword> findTopByEmailOrderByExpiryTimeDesc(String email);
}
