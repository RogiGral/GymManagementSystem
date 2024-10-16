package com.gymsystem.gms.service;


import com.gymsystem.gms.model.QrCode;


public interface QrCodeService {
    QrCode getQrCode(String uuid);
    QrCode createQrCode(String uuid, String encryptedData);
}
