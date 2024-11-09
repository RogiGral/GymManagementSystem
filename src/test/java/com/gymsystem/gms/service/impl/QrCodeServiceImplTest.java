package com.gymsystem.gms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.gymsystem.gms.model.QrCode;
import com.gymsystem.gms.repository.QrCodeRepository;
import com.gymsystem.gms.service.Impl.QrCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=test_jwt_secret",
        "api.stripe.key=test_stripe_key"
})
class QrCodeServiceImplTest {

    @Mock
    private QrCodeRepository qrCodeRepository;

    @InjectMocks
    private QrCodeServiceImpl qrCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetQrCode_Success() {
        String uuid = "test-uuid";
        QrCode qrCode = new QrCode();
        qrCode.setUuid(uuid);

        when(qrCodeRepository.findByUuid(uuid)).thenReturn(Optional.of(qrCode));

        QrCode result = qrCodeService.getQrCode(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        verify(qrCodeRepository, times(1)).findByUuid(uuid);
    }

    @Test
    void testGetQrCode_NotFound() {
        String uuid = "nonexistent-uuid";

        when(qrCodeRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            qrCodeService.getQrCode(uuid);
        });

        assertEquals("QR Code not found for UUID: " + uuid, exception.getMessage());
        verify(qrCodeRepository, times(1)).findByUuid(uuid);
    }

    @Test
    void testCreateQrCode_Success() {
        String uuid = "test-uuid";
        String encryptedData = "encrypted-data";
        QrCode qrCode = QrCode.builder().uuid(uuid).encryptedData(encryptedData).build();

        when(qrCodeRepository.save(any(QrCode.class))).thenReturn(qrCode);

        QrCode result = qrCodeService.createQrCode(uuid, encryptedData);

        assertNotNull(result);
        assertEquals(uuid, result.getUuid());
        assertEquals(encryptedData, result.getEncryptedData());
        assertNotNull(result.getExpirationDate());
        verify(qrCodeRepository, times(1)).save(any(QrCode.class));
    }

    @Test
    void testDeleteExpiredQRCodes() {
        LocalDateTime now = LocalDateTime.now();
        doNothing().when(qrCodeRepository).deleteAllByExpirationDateBefore(now);

        qrCodeService.deleteExpiredQRCodes();

        verify(qrCodeRepository, times(1)).deleteAllByExpirationDateBefore(now);
    }

    @Test
    void testDeleteExpiredQRCodes_NoDeletionForNonExpired() {
        LocalDateTime now = LocalDateTime.now().minusDays(1); // Expired QR codes only
        QrCode expiredQrCode = QrCode.builder().uuid("expired-uuid").expirationDate(now).build();
        QrCode activeQrCode = QrCode.builder().uuid("active-uuid").expirationDate(now.plusDays(1)).build();

        when(qrCodeRepository.findAll()).thenReturn(List.of(expiredQrCode, activeQrCode));

        qrCodeService.deleteExpiredQRCodes();

        verify(qrCodeRepository, times(1)).deleteAllByExpirationDateBefore(any(LocalDateTime.class));
        verify(qrCodeRepository, never()).delete(activeQrCode);
    }
}
