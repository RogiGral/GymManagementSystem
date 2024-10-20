package com.gymsystem.gms.controller;

import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.model.QrCode;
import com.gymsystem.gms.service.QrCodeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value ="/qrcode")
@AllArgsConstructor
public class QrCodeController extends ExceptionHandling {
    @Autowired
    private QrCodeService qrCodeService;

    @PostMapping("/add")
    //@PreAuthorize("hasAnyAuthority('qrcode:create')") //comment for testing
    public ResponseEntity<QrCode> addNewQrCode(
            @RequestParam("uuid") String uuid,
            @RequestParam("data") String data
    ){
        QrCode newQrCode = qrCodeService.createQrCode(uuid,data);
        return new ResponseEntity<>(newQrCode, HttpStatus.OK);
    }
    
    @GetMapping("/{uuid}")
    //@PreAuthorize("hasAnyAuthority('qrcode:read')") //comment for testing
    public ResponseEntity<QrCode> getAllWorkouts( @PathVariable("uuid") String uuid) {
        QrCode qrCode = qrCodeService.getQrCode(uuid);
        return new ResponseEntity<>(qrCode, OK);
    }


}
