package com.gymsystem.gms.service.impl;

import com.gymsystem.gms.enumeration.Role;
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
import com.gymsystem.gms.service.Impl.WorkoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=test_jwt_secret",
        "api.stripe.key=test_stripe_key"
})
class WorkoutServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private UserWorkoutRepository userWorkoutRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWorkouts_ShouldReturnListOfWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        workouts.add(new Workout());
        when(workoutRepository.findAll()).thenReturn(workouts);

        List<Workout> result = workoutService.getWorkouts();

        assertEquals(1, result.size());
        verify(workoutRepository, times(1)).findAll();
    }

    @Test
    void createWorkout_ShouldCreateAndReturnWorkout() throws UserNotFoundException, WorkoutDateException, WorkoutExistException {
        String workoutName = "Yoga";
        String trainerUsername = "trainer1";
        String roomNumber = "101";
        Integer capacity = 20;
        LocalDateTime workoutStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime workoutEndDate = workoutStartDate.plusHours(1);
        String workoutDifficulty = "EASY";

        User trainer = new User();
        trainer.setRole(Role.ROLE_COACH.toString());
        when(userRepository.findUserByUsername(trainerUsername)).thenReturn(trainer);
        when(workoutRepository.findWorkoutByWorkoutNameAndRoomNumberAndWorkoutEndDateAndWorkoutStartDateAndTrainerUsername(
                anyString(), anyString(), any(), any(), anyString())).thenReturn(null);

        Workout result = workoutService.createWorkout(workoutName, trainerUsername, roomNumber, capacity, workoutStartDate, workoutEndDate, workoutDifficulty);

        assertNotNull(result);
        assertEquals(workoutName, result.getWorkoutName());
        assertEquals(trainerUsername, result.getTrainerUsername());
        verify(workoutRepository, times(1)).save(result);
    }

    @Test
    void createWorkout_ShouldThrowUserNotFoundException() {
        String workoutName = "Yoga";
        String trainerUsername = "nonexistent_trainer";
        String roomNumber = "101";
        Integer capacity = 20;
        LocalDateTime workoutStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime workoutEndDate = workoutStartDate.plusHours(1);
        String workoutDifficulty = "EASY";

        when(userRepository.findUserByUsername(trainerUsername)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () ->
                workoutService.createWorkout(workoutName, trainerUsername, roomNumber, capacity, workoutStartDate, workoutEndDate, workoutDifficulty)
        );
    }

    @Test
    void updateWorkout_ShouldUpdateAndReturnWorkout() throws UserNotFoundException, WorkoutDateException, WorkoutExistException, WorkoutNotFoundException {
        Long workoutId = 1L;
        Workout existingWorkout = new Workout();
        existingWorkout.setId(workoutId);
        existingWorkout.setTrainerUsername("trainer1");

        User trainer = new User();
        trainer.setRole(Role.ROLE_COACH.toString());
        trainer.setUsername("trainer2");
        when(userRepository.findUserByUsername(anyString())).thenReturn(trainer);
        when(workoutRepository.findWorkoutById(workoutId)).thenReturn(existingWorkout);

        Workout result = workoutService.updateWorkout(workoutId, "New Yoga", "trainer2", "102", 30, 5, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), "MEDIUM");

        assertNotNull(result);
        assertEquals("New Yoga", result.getWorkoutName());
        verify(workoutRepository, times(1)).save(result);
    }

    @Test
    void deleteWorkout_ShouldDeleteWorkout() throws WorkoutNotFoundException {
        Long workoutId = 1L;
        Workout workout = new Workout();
        workout.setId(workoutId);
        List<UserWorkout> userWorkouts = new ArrayList<>();

        when(workoutRepository.findWorkoutById(workoutId)).thenReturn(workout);
        when(userWorkoutRepository.findAllByWorkoutId(workoutId)).thenReturn(userWorkouts);

        workoutService.deleteWorkout(workoutId);

        verify(userWorkoutRepository, times(1)).deleteAll(userWorkouts);
        verify(workoutRepository, times(1)).deleteById(workoutId);
    }

    @Test
    void validateStartEndDate_ShouldThrowWorkoutDateException_WhenEndDateIsBeforeStartDate() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = startDate.minusHours(1);

        assertThrows(WorkoutDateException.class, () -> workoutService.validateStartEndDate(startDate, endDate));
    }
}