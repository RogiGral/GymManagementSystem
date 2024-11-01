package com.gymsystem.gms.service.Impl;


import com.gymsystem.gms.model.QrCode;
import com.gymsystem.gms.repository.QrCodeRepository;
import com.gymsystem.gms.service.QrCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class QrCodeServiceImpl implements QrCodeService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final QrCodeRepository qrCodeRepository;

    @Autowired
    public QrCodeServiceImpl(QrCodeRepository qrCodeRepository) {
        this.qrCodeRepository = qrCodeRepository;
    }

    @Override
    public QrCode getQrCode(String uuid) {
        LOGGER.info("Fetching QR code with UUID: {}", uuid);
        return qrCodeRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("QR Code not found for UUID: " + uuid));
    }

    @Override
    public QrCode createQrCode(String uuid, String encryptedData) {
        LOGGER.info("Creating QR code with UUID: {}", uuid);

        // Create a new QR code entity
        QrCode qrCode = QrCode.builder()
                .uuid(uuid)
                .encryptedData(encryptedData)
                .build();

        // Set the expiration date to 2 minutes from now
        qrCode.setExpirationDate();

        // Save the QR code to the database
        return qrCodeRepository.save(qrCode);
    }

    @Scheduled(fixedRate = 60000) // 60 seconds
    public void deleteExpiredQRCodes() {
        LocalDateTime now = LocalDateTime.now();
        qrCodeRepository.deleteAllByExpirationDateBefore(now);
    }
}

