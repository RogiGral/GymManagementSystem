package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.model.WorkoutRoom;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutRoomService {
    List<WorkoutRoom> getWorkoutRooms();
    WorkoutRoom createWorkoutRoom(String workoutRoomName, Integer capacity);
    WorkoutRoom updateWorkoutRoom(Long id,String oldWorkoutRoomName,String workoutRoomName, Integer capacity) throws WorkoutRoomNotFoundException;
    void deleteWorkoutRoom(Long id) throws WorkoutRoomNotFoundException;
}
