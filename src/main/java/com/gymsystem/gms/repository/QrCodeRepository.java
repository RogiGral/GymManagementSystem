package com.gymsystem.gms.repository;

import com.gymsystem.gms.model.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode,Long> {

    Optional<QrCode> findByUuid(String uuid);
    void deleteAllByExpirationDateBefore(LocalDateTime now);
}
