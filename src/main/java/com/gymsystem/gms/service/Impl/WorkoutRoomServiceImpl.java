package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.exceptions.model.WorkoutRoomNotFoundException;
import com.gymsystem.gms.model.UserWorkout;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.model.WorkoutRoom;
import com.gymsystem.gms.repository.UserWorkoutRepository;
import com.gymsystem.gms.repository.WorkoutRepository;
import com.gymsystem.gms.repository.WorkoutRoomRepository;
import com.gymsystem.gms.service.WorkoutRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.gymsystem.gms.constraints.WorkoutRoomConstraints.NO_WORKOUT_ROOM_FOUND_BY_ID;

@Service
@Transactional
public class WorkoutRoomServiceImpl implements WorkoutRoomService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final WorkoutRoomRepository workoutRoomRepository;
    private final UserWorkoutRepository userWorkoutRepository;
    private final WorkoutRepository workoutRepository;

    public WorkoutRoomServiceImpl( WorkoutRepository workoutRepository, WorkoutRoomRepository workoutRoomRepository, UserWorkoutRepository userWorkoutRepository) {
        this.workoutRoomRepository = workoutRoomRepository;
        this.userWorkoutRepository = userWorkoutRepository;
        this.workoutRepository = workoutRepository;
    }

    @Override
    public List<WorkoutRoom> getWorkoutRooms() {
        return workoutRoomRepository.findAll();
    }

    @Override
    public WorkoutRoom createWorkoutRoom(String workoutRoomName, Integer capacity) {
        WorkoutRoom workoutRoom = new WorkoutRoom();
        workoutRoom.setCapacity(capacity);
        workoutRoom.setWorkoutRoomName(workoutRoomName);
        workoutRoomRepository.save(workoutRoom);
        LOGGER.info("Workout room has ben created");
        return workoutRoom;
    }

    @Override
    public WorkoutRoom updateWorkoutRoom(Long id,String oldWorkoutRoomName, String workoutRoomName, Integer capacity) throws WorkoutRoomNotFoundException {
        WorkoutRoom workoutRoom = findWorkoutRoomById(id);
        workoutRoom.setCapacity(capacity);
        workoutRoom.setWorkoutRoomName(workoutRoomName);
        workoutRoomRepository.save(workoutRoom);
        LOGGER.info("Workout room has ben updated");
        return workoutRoom;
    }

    @Override
    public void deleteWorkoutRoom(Long id) throws WorkoutRoomNotFoundException {
        WorkoutRoom workoutRoom = findWorkoutRoomById(id);
        Long workoutRoomId = workoutRoom.getId();
        List<Workout> workouts = workoutRepository.findWorkoutsByRoomNumber(workoutRoomId);
        workouts.forEach(workout -> {
            Long workoutId = workout.getId();
            List<UserWorkout> userWorkoutsList = userWorkoutRepository.findAllByWorkoutId(workoutId);
            userWorkoutRepository.deleteAll(userWorkoutsList);
        });
        workoutRepository.deleteAll(workouts);
        workoutRoomRepository.deleteById(id);
        LOGGER.info("Workout room and all workouts has been deleted");
    }

    private WorkoutRoom findWorkoutRoomById(Long id) throws WorkoutRoomNotFoundException {
        WorkoutRoom workoutRoom = workoutRoomRepository.findWorkoutRoomById(id);
        if(workoutRoom == null){
            throw new WorkoutRoomNotFoundException(NO_WORKOUT_ROOM_FOUND_BY_ID+id);
        }
        return workoutRoom;
    }


}
