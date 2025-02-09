package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.WorkoutRoomNotFoundException;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.model.WorkoutRoom;
import com.gymsystem.gms.service.WorkoutRoomService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value ="/workout-room")
@AllArgsConstructor
public class WorkoutRoomController extends ExceptionHandling {
    @Autowired
    private WorkoutRoomService workoutRoomService;

    @PostMapping("/add")
    //@PreAuthorize("hasAnyAuthority('workoutRoom:create')") //comment for testing
    public ResponseEntity<WorkoutRoom> addNewWorkoutRoom(
            @RequestParam("workoutRoomName") String workoutRoomName,
            @RequestParam("capacity") Integer capacity
    ){
        WorkoutRoom newWorkoutRoom = workoutRoomService.createWorkoutRoom(workoutRoomName,capacity);
        return new ResponseEntity<>(newWorkoutRoom, HttpStatus.OK);
    }

    @PostMapping("/update")
    //@PreAuthorize("hasAnyAuthority('workoutRoom:update')") //comment for testing
    public ResponseEntity<WorkoutRoom> updateWorkout(
            @RequestParam("workoutRoomId") Long workoutRoomId,
            @RequestParam("oldWorkoutRoomNme") String oldWorkoutRoomNme,
            @RequestParam("workoutRoomName") String workoutRoomName,
            @RequestParam("capacity") Integer capacity
    ) throws WorkoutRoomNotFoundException {
        WorkoutRoom newWorkout = workoutRoomService.updateWorkoutRoom(workoutRoomId,oldWorkoutRoomNme,workoutRoomName,capacity);
        return new ResponseEntity<>(newWorkout, HttpStatus.OK);
    }
    @GetMapping("/list")
    //@PreAuthorize("hasAnyAuthority('workoutRoom:read')") //comment for testing
    public ResponseEntity<List<WorkoutRoom>> getAllWorkouts() {
        List<WorkoutRoom> workoutRooms = workoutRoomService.getWorkoutRooms();
        return new ResponseEntity<>(workoutRooms, OK);
    }
    @DeleteMapping("/delete/{id}")
    //@PreAuthorize("hasAnyAuthority('workoutRoom:delete')") //comment for testing
    public ResponseEntity<HttpResponse> deleteWorkout(@PathVariable("id") Long id) throws WorkoutRoomNotFoundException {
        workoutRoomService.deleteWorkoutRoom(id);
        return response(OK, "WORKOUT_ROOM_DELETED_SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


}
