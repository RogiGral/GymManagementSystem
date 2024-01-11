package com.gymsystem.gms.repository;

import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout,Long> {
    Workout findWorkoutById(Long id);
    List<Workout> findWorkoutsByRoomNumber(Long roomNumber);
    Workout findWorkoutByWorkoutNameAndRoomNumberAndWorkoutEndDateAndWorkoutStartDateAndTrainerUsername(String workoutName, String roomNumber, LocalDateTime workoutEndDate, LocalDateTime workoutStartDate, String trainerUsername);
}
