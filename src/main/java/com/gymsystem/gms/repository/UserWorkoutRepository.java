package com.gymsystem.gms.repository;

import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserWorkout;
import com.gymsystem.gms.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWorkoutRepository extends JpaRepository<UserWorkout,Long> {
    @Query(value = "SELECT * FROM user_workout WHERE user_id = :userId",nativeQuery = true)
    List<UserWorkout> findAllByUserId(@Param("userId") Long userId);

    UserWorkout findUserWorkoutById(Long id);
    UserWorkout findUserWorkoutByUserAndWorkout(User user, Workout workout);

    @Query(value = "SELECT * FROM user_workout WHERE workout_id = :workoutId",nativeQuery = true)
    List<UserWorkout> findAllByWorkoutId(@Param("workoutId") Long workoutId);
}
