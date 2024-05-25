package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserWorkout;
import com.gymsystem.gms.model.Workout;

import java.util.Date;
import java.util.List;

public interface UserWorkoutService {
    List<UserWorkout> getAllUserWorkouts(Long userId);
    UserWorkout addUserToWorkout(Long userId, Long workoutId) throws WorkoutNotFoundException, WorkoutIsFullException, UserIsAlreadyInWorkoutException, UserNotFoundException;
    void deleteUserWorkout(Long userWorkoutId) throws WorkoutNotFoundException;
    void deleteUserWorkout(Long userId, Long workoutId) throws WorkoutNotFoundException, UserNotFoundException;
    List<User> listAllUsersJoined(Long workoutId);
}
