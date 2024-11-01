package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.service.ScoreService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value ="/score")
@AllArgsConstructor
public class ScoreController extends ExceptionHandling {
    @Autowired
    private ScoreService scoreService;

    @GetMapping("/get/{username}")
    public ResponseEntity<BigDecimal> getUserScore(
            @PathVariable("username") String  username
    ) {
        BigDecimal score = scoreService.getUserScore(username);
        return new ResponseEntity<>(score, OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('workout:update')") //comment for testing
    public ResponseEntity<HttpResponse> addToUserScore(
            @RequestParam("username") String  username,
            @RequestParam("score") BigDecimal score
    ) throws UserNotFoundException {
        scoreService.addPointsToScore(username,score);
        return response(OK, "POINTS_ADDED_TO_SCORE");
    }
    @PostMapping("/remove")
    @PreAuthorize("hasAnyAuthority('workout:update')") //comment for testing
    public ResponseEntity<HttpResponse> removeUserScore(
            @RequestParam("username") String  username,
            @RequestParam("score") BigDecimal score
    ) throws UserNotFoundException {
        scoreService.removePointsFromScore(username,score);
        return response(OK, "POINTS_REMOVED_TO_SCORE");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


}
