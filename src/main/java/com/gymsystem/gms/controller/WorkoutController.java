package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.exceptions.model.WorkoutDateException;
import com.gymsystem.gms.exceptions.model.WorkoutExistException;
import com.gymsystem.gms.exceptions.model.WorkoutNotFoundException;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.service.WorkoutService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value ="/workout")
@AllArgsConstructor
public class WorkoutController extends ExceptionHandling {
    @Autowired
    private WorkoutService workoutService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('workout:create')") //comment for testing
    public ResponseEntity<Workout> addNewWorkout(
            @RequestParam("workoutName") String workoutName,
            @RequestParam("trainerUsername") String trainerUsername,
            @RequestParam("roomNumber") String roomNumber,
            @RequestParam("capacity") Integer capacity,
            @RequestParam("workoutStartDate") String workoutStartDateTimeString,
            @RequestParam("workoutEndDate") String workoutEndDateTimeString,
            @RequestParam("workoutDifficulty") String workoutDifficulty
    ) throws WorkoutDateException, WorkoutExistException, UserNotFoundException {

        LocalDateTime workoutStartDate = LocalDateTime.parse(workoutStartDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime workoutEndDate = LocalDateTime.parse(workoutEndDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Workout newWorkout = workoutService.createWorkout(workoutName, trainerUsername, roomNumber, capacity, workoutStartDate, workoutEndDate,workoutDifficulty);
        return new ResponseEntity<>(newWorkout, HttpStatus.OK);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('workout:update')") //comment for testing
    public ResponseEntity<Workout> updateWorkout(
            @RequestParam("workoutId") Long workoutId,
            @RequestParam("workoutName") String newWorkoutName,
            @RequestParam("trainerUsername") String newTrainerUsername,
            @RequestParam("roomNumber") String newRoomNumber,
            @RequestParam("capacity") String capacityStr,
            @RequestParam("participantsNumber") String participantsNumberStr,
            @RequestParam("workoutStartDate") String newWorkoutStartDateTimeString,
            @RequestParam("workoutEndDate") String newWorkoutEndDateTimeString,
            @RequestParam("workoutDifficulty") String newWorkoutDifficulty
    ) throws WorkoutDateException, WorkoutExistException, UserNotFoundException, WorkoutNotFoundException {

        Integer capacity = Integer.parseInt(capacityStr);
        Integer participantsNumber = Integer.parseInt(participantsNumberStr);

        LocalDateTime newWorkoutStartDate = LocalDateTime.parse(newWorkoutStartDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime newWorkoutEndDate = LocalDateTime.parse(newWorkoutEndDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Workout newWorkout = workoutService.updateWorkout(workoutId, newWorkoutName, newTrainerUsername, newRoomNumber, capacity, participantsNumber, newWorkoutStartDate, newWorkoutEndDate, newWorkoutDifficulty);
        return new ResponseEntity<>(newWorkout, HttpStatus.OK);
    }
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('workout:read')") //comment for testing
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        List<Workout> workouts = workoutService.getWorkouts();
        return new ResponseEntity<>(workouts, OK);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('workout:delete')") //comment for testing
    public ResponseEntity<HttpResponse> deleteWorkout(@PathVariable("id") Long id) throws WorkoutNotFoundException {
        workoutService.deleteWorkout(id);
        return response(OK, "WORKOUT_DELETED_SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


}
