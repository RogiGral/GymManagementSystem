package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.service.WorkoutService;
import com.gymsystem.gms.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value ="/workout")
@AllArgsConstructor
public class WorkoutController extends ExceptionHandling {
    @Autowired
    private WorkoutService workoutService;

    @PostMapping("/add")
    //@PreAuthorize("hasAuthority('workout:crud')") //comment for testing
    public ResponseEntity<Workout> addNewWorkout(@RequestParam("workoutName") String workoutName,
                                                 @RequestParam("trainerUsername") String trainerUsername,
                                                 @RequestParam("roomNumber") String roomNumber,
                                                 @RequestParam("capacity") Integer capacity,
                                                 @RequestParam("workoutStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date workoutStartDate,
                                                 @RequestParam("workoutEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date workoutEndDate) throws WorkoutDateException, WorkoutExistException, UserNotFoundException {
        Workout newWorkout = workoutService.createWorkout(workoutName,trainerUsername,roomNumber,capacity,workoutStartDate,workoutEndDate);
        return new ResponseEntity<>(newWorkout, OK);
    }
    @PostMapping("/update")
    //@PreAuthorize("hasAuthority('workout:crud')") //comment for testing
    public ResponseEntity<Workout> updateWorkout(@RequestParam("workoutId") Long workoutId,
                                                 @RequestParam("newWorkoutName") String newWorkoutName,
                                                 @RequestParam("newTrainerUsername") String newTrainerUsername,
                                                 @RequestParam("newRoomNumber") String newRoomNumber,
                                                 @RequestParam("capacity") Integer capacity,
                                                 @RequestParam("participantsNumber") Integer participantsNumber,
                                                 @RequestParam("newWorkoutStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date newWorkoutStartDate,
                                                 @RequestParam("newWorkoutEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date newWorkoutEndDate) throws WorkoutDateException, WorkoutExistException, UserNotFoundException, WorkoutNotFoundException {
        Workout newWorkout = workoutService.updateWorkout(workoutId,newWorkoutName,newTrainerUsername,newRoomNumber,capacity,participantsNumber,newWorkoutStartDate,newWorkoutEndDate);
        return new ResponseEntity<>(newWorkout, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        List<Workout> workouts = workoutService.getWorkouts();
        return new ResponseEntity<>(workouts, OK);
    }
    @DeleteMapping("/delete/{id}")
    //@PreAuthorize("hasAnyAuthority('workout:crud')") //comment for testing
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") Long id) throws WorkoutNotFoundException {
        workoutService.deleteWorkout(id);
        return response(OK, "WORKOUT_DELETED_SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


}
