package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.exceptions.model.WorkoutDateException;
import com.gymsystem.gms.exceptions.model.WorkoutExistException;
import com.gymsystem.gms.exceptions.model.WorkoutNotFoundException;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.Workout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface WorkoutService {
    List<Workout> getWorkouts();
    Workout createWorkout(String workoutName, String trainerUsername, String roomNumber, Integer capacity, LocalDateTime workoutStartDate, LocalDateTime workoutEndDate) throws WorkoutDateException, WorkoutExistException, UserNotFoundException;
    Workout updateWorkout(Long id,String newWorkoutName, String newTrainerUsername,String newRoomNumber,Integer newCapacity,Integer newParticipantsNumber, LocalDateTime newWorkoutStartDate, LocalDateTime newWorkoutEndDate) throws WorkoutDateException, WorkoutExistException, UserNotFoundException, WorkoutNotFoundException;
    void deleteWorkout(Long id) throws WorkoutNotFoundException;
}
