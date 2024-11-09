package com.gymsystem.gms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import com.gymsystem.gms.model.QrCode;
import com.gymsystem.gms.repository.QrCodeRepository;
import com.gymsystem.gms.service.Impl.QrCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QrCodeServiceImplTests {

    @Mock
    private QrCodeRepository qrCodeRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private QrCodeServiceImpl qrCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetQrCode_Found() {
        // Arrange
        String uuid = "1234";
        QrCode qrCode = new QrCode();
        qrCode.setUuid(uuid);

        when(qrCodeRepository.findByUuid(uuid)).thenReturn(Optional.of(qrCode));

        // Act
        QrCode result = qrCodeService.getQrCode(uuid);

        // Assert
        assertEquals(qrCode, result);
        verify(logger).info("Fetching QR code with UUID: {}", uuid);
        verify(qrCodeRepository).findByUuid(uuid);
    }

    @Test
    void testGetQrCode_NotFound() {
        // Arrange
        String uuid = "5678";
        when(qrCodeRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> qrCodeService.getQrCode(uuid));
        assertEquals("QR Code not found for UUID: " + uuid, exception.getMessage());
        verify(logger).info("Fetching QR code with UUID: {}", uuid);
        verify(qrCodeRepository).findByUuid(uuid);
    }

    @Test
    void testCreateQrCode() {
        // Arrange
        String uuid = "1234";
        String encryptedData = "encryptedData";

        QrCode qrCode = QrCode.builder()
                .uuid(uuid)
                .encryptedData(encryptedData)
                .build();
        qrCode.setExpirationDate(); // Assuming this method sets an expiration date.

        when(qrCodeRepository.save(any(QrCode.class))).thenReturn(qrCode);

        // Act
        QrCode result = qrCodeService.createQrCode(uuid, encryptedData);

        // Assert
        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(encryptedData, result.getEncryptedData());
        assertNotNull(result.getExpirationDate());
        verify(logger).info("Creating QR code with UUID: {}", uuid);
        verify(qrCodeRepository).save(any(QrCode.class));
    }

    @Test
    void testDeleteExpiredQRCodes() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        qrCodeService.deleteExpiredQRCodes();

        // Assert
        verify(qrCodeRepository).deleteAllByExpirationDateBefore(now);
    }

}
