package com.gymsystem.gms.repository;

import com.gymsystem.gms.model.WorkoutRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRoomRepository extends JpaRepository<WorkoutRoom,Long> {
    WorkoutRoom findWorkoutRoomById(Long id);
}
