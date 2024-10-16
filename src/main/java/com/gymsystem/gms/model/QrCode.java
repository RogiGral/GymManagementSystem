package com.gymsystem.gms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "qrcode")
public class QrCode extends AuditRecords implements Serializable {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "QRCODE_SEQ")
    @SequenceGenerator(name = "QRCODE_SEQ", sequenceName = "QRCODE_SEQ", allocationSize = 1)
    @Column(insertable = false, updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String encryptedData;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    public void setExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusMinutes(2);
    }
}

