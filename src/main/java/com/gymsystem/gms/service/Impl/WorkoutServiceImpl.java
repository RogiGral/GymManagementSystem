package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.enumeration.Role;
import com.gymsystem.gms.enumeration.WorkoutDifficulty;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.exceptions.model.WorkoutDateException;
import com.gymsystem.gms.exceptions.model.WorkoutExistException;
import com.gymsystem.gms.exceptions.model.WorkoutNotFoundException;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserWorkout;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.repository.UserRepository;
import com.gymsystem.gms.repository.UserWorkoutRepository;
import com.gymsystem.gms.repository.WorkoutRepository;
import com.gymsystem.gms.service.WorkoutService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.gymsystem.gms.constraints.UserImplConstant.NO_TRAINER_FOUND_BY_USERNAME;
import static com.gymsystem.gms.constraints.UserImplConstant.USER_IS_NOT_TRAINER;
import static com.gymsystem.gms.constraints.WorkoutConstraint.*;

@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final UserWorkoutRepository userWorkoutRepository;

    public WorkoutServiceImpl(UserRepository userRepository, WorkoutRepository workoutRepository, UserWorkoutRepository userWorkoutRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.userWorkoutRepository = userWorkoutRepository;
    }

    @Override
    public List<Workout> getWorkouts() {
        return workoutRepository.findAll();
    }

    @Override
    public Workout createWorkout(String workoutName, String trainerUsername, String roomNumber, Integer capacity, LocalDateTime workoutStartDate, LocalDateTime workoutEndDate, String workoutDifficulty) throws WorkoutDateException, WorkoutExistException, UserNotFoundException {
        checkIfTrainerExists(trainerUsername);
        validateStartEndDate(workoutStartDate,workoutEndDate);
        checkIfWorkoutExists(StringUtils.EMPTY,workoutName,trainerUsername,roomNumber,workoutStartDate,workoutEndDate);
        Workout workout = new Workout();
        workout.setWorkoutName(workoutName);
        workout.setTrainerUsername(trainerUsername);
        workout.setRoomNumber(roomNumber);
        workout.setWorkoutStartDate(workoutStartDate);
        workout.setWorkoutEndDate(workoutEndDate);
        workout.setCapacity(capacity);
        workout.setParticipantsNumber(0);
        workout.setWorkoutDifficulty(getWorkoutDifficultyEnumName(workoutDifficulty));

        workoutRepository.save(workout);
        LOGGER.info("Workout has been created");
        //wyslij mail do trenera odnosnie treningu; docelowo dodać do kalenarza
        return workout;
    }
    // Todo do poprawy, co w przypadku kiedy poda się nowy workoutName a reszta będzie taka sama;
    //  solution, można zablokowac zmienianie innych danych poza nazwą - nie zbyt pasuje ale zawsze coś
    @Override
    public Workout updateWorkout(Long id, String newWorkoutName, String newTrainerUsername, String newRoomNumber,Integer newCapacity,Integer newParticipantsNumber, LocalDateTime newWorkoutStartDate, LocalDateTime newWorkoutEndDate, String newWorkoutDifficulty) throws WorkoutDateException, WorkoutExistException, UserNotFoundException, WorkoutNotFoundException {
        checkIfTrainerExists(newTrainerUsername);
        validateStartEndDate(newWorkoutStartDate,newWorkoutEndDate);
        Workout workout = findWorkoutById(id);
        workout.setWorkoutName(newWorkoutName);
        workout.setTrainerUsername(newTrainerUsername);
        workout.setRoomNumber(newRoomNumber);
        workout.setWorkoutStartDate(newWorkoutStartDate);
        workout.setWorkoutEndDate(newWorkoutEndDate);
        workout.setCapacity(newCapacity);
        workout.setParticipantsNumber(newParticipantsNumber);
        workout.setWorkoutDifficulty(getWorkoutDifficultyEnumName(newWorkoutDifficulty));
        workoutRepository.save(workout);
        LOGGER.info("Workout has been updated");
        return workout;
    }

    @Override
    public void deleteWorkout(Long id) throws WorkoutNotFoundException {
        Workout workout = findWorkoutById(id);
        Long workoutId = workout.getId();
        List<UserWorkout> userWorkoutsList = userWorkoutRepository.findAllByWorkoutId(workoutId);
        userWorkoutRepository.deleteAll(userWorkoutsList);
        workoutRepository.deleteById(id);
        LOGGER.info("Workout has been deleted");
    }

    private Workout findWorkoutById(Long id) throws WorkoutNotFoundException {
        Workout workout = workoutRepository.findWorkoutById(id);
        if(workout == null){
            throw new WorkoutNotFoundException(NO_WORKOUT_FOUND_BY_ID+id);
        }
        return workout;
    }

    private void checkIfTrainerExists(String trainerUsername) throws UserNotFoundException {
        User trainer = userRepository.findUserByUsername(trainerUsername);
        if (trainer == null) {
            throw new UserNotFoundException(NO_TRAINER_FOUND_BY_USERNAME + trainerUsername);
        }
        if(!(Objects.equals(trainer.getRole(), Role.ROLE_COACH.toString()))){
            throw new UserNotFoundException(USER_IS_NOT_TRAINER  + trainerUsername);
        }
    }

    public void validateStartEndDate(LocalDateTime workoutStartDate, LocalDateTime workoutEndDate) throws WorkoutDateException {
        if(workoutEndDate==null || workoutStartDate==null)return;
        if(workoutEndDate.isBefore(workoutStartDate)){
            throw new WorkoutDateException(WORKOUT_DATE_INVALID);
        }
    }

    private void checkIfWorkoutExists(String currentWorkout, String workoutName, String trainerUsername, String roomNumber, LocalDateTime workoutStartDate, LocalDateTime workoutEndDate) throws WorkoutExistException {
        if(currentWorkout.isEmpty()){
            Workout workout = workoutRepository.findWorkoutByWorkoutNameAndRoomNumberAndWorkoutEndDateAndWorkoutStartDateAndTrainerUsername(workoutName,roomNumber,workoutEndDate,workoutStartDate,trainerUsername);
            if (workout != null) {
                throw new WorkoutExistException(WORKOUT_ALREADY_EXISTS + workoutName);
            }
        }
        else{
            Workout workout = workoutRepository.findWorkoutByWorkoutNameAndRoomNumberAndWorkoutEndDateAndWorkoutStartDateAndTrainerUsername(currentWorkout,roomNumber,workoutEndDate,workoutStartDate,trainerUsername);
            if (workout != null) {
                throw new WorkoutExistException(WORKOUT_ALREADY_EXISTS + workoutName);
            }
        }
    }

    private WorkoutDifficulty getWorkoutDifficultyEnumName(String workoutDifficulty) {
        return WorkoutDifficulty.valueOf(workoutDifficulty.toUpperCase());
    }
}
