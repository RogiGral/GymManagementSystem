package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.UserIsAlreadyInWorkoutException;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.exceptions.model.WorkoutIsFullException;
import com.gymsystem.gms.exceptions.model.WorkoutNotFoundException;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserWorkout;
import com.gymsystem.gms.service.UserWorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value ="/user-workout")
@AllArgsConstructor
public class UserWorkoutController extends ExceptionHandling {
    @Autowired
    UserWorkoutService userWorkoutService;

    @PostMapping("/add")
    public ResponseEntity<UserWorkout> joinWorkout(@RequestParam("userId") Long userId,
                                                   @RequestParam("workoutId") Long workoutId) throws WorkoutNotFoundException, WorkoutIsFullException, UserIsAlreadyInWorkoutException, UserNotFoundException {
        UserWorkout newUserWorkout = userWorkoutService.addUserToWorkout(userId,workoutId);
        return new ResponseEntity<>(newUserWorkout, OK);
    }
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<UserWorkout>> getAllUserWorkouts(@PathVariable("userId") Long userId) {
        List<UserWorkout> userWorkouts = userWorkoutService.getAllUserWorkouts(userId);
        return new ResponseEntity<>(userWorkouts, OK);
    }
    @GetMapping("/list_joined/{workoutId}")
    public ResponseEntity<List<User>> listAllUsersJoined(@PathVariable("workoutId") Long workoutId) {
        List<User> users = userWorkoutService.listAllUsersJoined(workoutId);
        return new ResponseEntity<>(users, OK);
    }
    @DeleteMapping("/delete/{userWorkoutId}")
    public ResponseEntity<HttpResponse> leaveWorkout(@PathVariable("userWorkoutId") Long userWorkoutId) throws WorkoutNotFoundException {
            userWorkoutService.deleteUserWorkout(userWorkoutId);
        return response("USER_LEFT_WORKOUT");
    }
    @DeleteMapping("/delete/{userId}/{workoutId}")
    public ResponseEntity<HttpResponse> removeUserFromWorkout(@PathVariable("userId") Long userId,
                                                              @PathVariable("workoutId") Long workoutId) throws WorkoutNotFoundException, UserNotFoundException {
        userWorkoutService.deleteUserWorkout(userId,workoutId);
        return response("USER_REMOVED_FROM_WORKOUT");
    }

    private ResponseEntity<HttpResponse> response(String message) {
        return new ResponseEntity<>(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase().toUpperCase(),
                message), HttpStatus.OK);
    }


}
