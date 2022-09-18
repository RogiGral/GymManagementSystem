package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.enumeration.Role;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.exceptions.model.WorkoutDateException;
import com.gymsystem.gms.exceptions.model.WorkoutExistException;
import com.gymsystem.gms.exceptions.model.WorkoutNotFoundException;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.repository.UserRepository;
import com.gymsystem.gms.repository.WorkoutRepository;
import com.gymsystem.gms.service.WorkoutService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.gymsystem.gms.constraints.UserImplConstant.*;
import static com.gymsystem.gms.constraints.WorkoutConstraint.*;

@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private WorkoutRepository workoutRepository;

    public WorkoutServiceImpl(UserRepository userRepository, WorkoutRepository workoutRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
    }

    @Override
    public List<Workout> getWorkouts() {
        return workoutRepository.findAll();
    }


    //Todo  sprawdzać czy trener nie ma jakiś zajęć w tych godzinach
    //Todo sprawdzac czy sala nie jest zajęta w tych godzinach
    @Override
    public Workout createWorkout(String workoutName, String trainerUsername, String roomNumber,Integer capacity, Date workoutStartDate, Date workoutEndDate) throws WorkoutDateException, WorkoutExistException, UserNotFoundException {
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
        workoutRepository.save(workout);
        //wyslij mail do trenera odnosnie treningu; docelowo dodać do kalenarza
        return workout;
    }
    // Todo do poprawy, co w przypadku kiedy poda się nowy workoutName a reszta będzie taka sama;
    //  solution, można zablokowac zmienianie innych danych poza nazwą - nie zbyt pasuje ale zawsze coś
    @Override
    public Workout updateWorkout(Long id, String newWorkoutName, String newTrainerUsername, String newRoomNumber,Integer newCapacity,Integer newParticipantsNumber, Date newWorkoutStartDate, Date newWorkoutEndDate) throws WorkoutDateException, WorkoutExistException, UserNotFoundException, WorkoutNotFoundException {
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
        workoutRepository.save(workout);
        return workout;
    }

    @Override
    public void deleteWorkout(Long id) throws WorkoutNotFoundException {
        findWorkoutById(id);
        workoutRepository.deleteById(id);
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
        if(trainer.getRole()==Role.ROLE_COACH.toString()){
            throw new UserNotFoundException(USER_IS_NOT_TRAINER  + trainerUsername);
        }
    }

    private void validateStartEndDate(Date workoutStartDate, Date workoutEndDate) throws WorkoutDateException {
        if(workoutEndDate==null || workoutStartDate==null)return;
        if(workoutEndDate.before(workoutStartDate)){
            throw new WorkoutDateException(WORKOUT_DATE_INVALID);
        }
    }

    private Workout checkIfWorkoutExists(String currentWorkout, String workoutName, String trainerUsername, String roomNumber, Date workoutStartDate, Date workoutEndDate) throws WorkoutExistException {
        if(currentWorkout.isEmpty()){
            Workout workout = workoutRepository.findWorkoutByWorkoutNameAndRoomNumberAndWorkoutEndDateAndWorkoutStartDateAndTrainerUsername(workoutName,roomNumber,workoutEndDate,workoutStartDate,trainerUsername);
            if (workout != null) {
                throw new WorkoutExistException(WORKOUT_ALREADY_EXISTS + workoutName);
            }
            return workout;
        }
        else{
            Workout workout = workoutRepository.findWorkoutByWorkoutNameAndRoomNumberAndWorkoutEndDateAndWorkoutStartDateAndTrainerUsername(currentWorkout,roomNumber,workoutEndDate,workoutStartDate,trainerUsername);
            if (workout != null) {
                throw new WorkoutExistException(WORKOUT_ALREADY_EXISTS + workoutName);
            }
            return workout;
        }
    }
}
