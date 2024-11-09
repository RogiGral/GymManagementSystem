package com.gymsystem.gms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymsystem.gms.exceptions.model.WorkoutRoomNotFoundException;
import com.gymsystem.gms.model.UserWorkout;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.model.WorkoutRoom;
import com.gymsystem.gms.repository.UserRepository;
import com.gymsystem.gms.repository.UserWorkoutRepository;
import com.gymsystem.gms.repository.WorkoutRepository;
import com.gymsystem.gms.repository.WorkoutRoomRepository;
import com.gymsystem.gms.service.Impl.WorkoutRoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class WorkoutRoomServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutRoomRepository workoutRoomRepository;

    @Mock
    private UserWorkoutRepository userWorkoutRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutRoomServiceImpl workoutRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWorkoutRooms() {
        // Arrange
        List<WorkoutRoom> workoutRooms = new ArrayList<>();
        workoutRooms.add(new WorkoutRoom());

        when(workoutRoomRepository.findAll()).thenReturn(workoutRooms);

        // Act
        List<WorkoutRoom> result = workoutRoomService.getWorkoutRooms();

        // Assert
        assertEquals(workoutRooms.size(), result.size());
        verify(workoutRoomRepository).findAll();
    }

    @Test
    void testCreateWorkoutRoom() {
        // Arrange
        String roomName = "Yoga Room";
        int capacity = 20;

        WorkoutRoom workoutRoom = new WorkoutRoom();
        workoutRoom.setWorkoutRoomName(roomName);
        workoutRoom.setCapacity(capacity);

        when(workoutRoomRepository.save(any(WorkoutRoom.class))).thenReturn(workoutRoom);

        // Act
        WorkoutRoom result = workoutRoomService.createWorkoutRoom(roomName, capacity);

        // Assert
        assertEquals(roomName, result.getWorkoutRoomName());
        assertEquals(capacity, result.getCapacity());
        verify(workoutRoomRepository).save(any(WorkoutRoom.class));
    }

    @Test
    void testUpdateWorkoutRoom() throws WorkoutRoomNotFoundException {
        // Arrange
        Long roomId = 1L;
        String newName = "New Room Name";
        String oldName = "Old Room Name";
        int capacity = 30;

        WorkoutRoom workoutRoom = new WorkoutRoom();
        workoutRoom.setId(roomId);
        workoutRoom.setWorkoutRoomName(oldName);

        when(workoutRoomRepository.findWorkoutRoomById(roomId)).thenReturn(workoutRoom);
        when(workoutRoomRepository.save(any(WorkoutRoom.class))).thenReturn(workoutRoom);

        // Act
        WorkoutRoom result = workoutRoomService.updateWorkoutRoom(roomId, oldName, newName, capacity);

        // Assert
        assertEquals(newName, result.getWorkoutRoomName());
        assertEquals(capacity, result.getCapacity());
        verify(workoutRoomRepository).save(any(WorkoutRoom.class));
    }

    @Test
    void testUpdateWorkoutRoom_NotFound() {
        // Arrange
        Long roomId = 1L;
        String newName = "New Room Name";
        int capacity = 30;

        when(workoutRoomRepository.findWorkoutRoomById(roomId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(WorkoutRoomNotFoundException.class, () ->
                workoutRoomService.updateWorkoutRoom(roomId, "Old Room Name", newName, capacity));
        assertEquals("No workout room found for ID: " + roomId, exception.getMessage());
    }

    @Test
    void testDeleteWorkoutRoom() throws WorkoutRoomNotFoundException {
        // Arrange
        Long roomId = 1L;
        WorkoutRoom workoutRoom = new WorkoutRoom();
        workoutRoom.setId(roomId);

        Workout workout = new Workout();
        workout.setId(1L);

        List<Workout> workouts = List.of(workout);
        List<UserWorkout> userWorkouts = List.of(new UserWorkout());

        when(workoutRoomRepository.findWorkoutRoomById(roomId)).thenReturn(workoutRoom);
        when(workoutRepository.findWorkoutsByRoomNumber(roomId)).thenReturn(workouts);
        when(userWorkoutRepository.findAllByWorkoutId(workout.getId())).thenReturn(userWorkouts);

        // Act
        workoutRoomService.deleteWorkoutRoom(roomId);

        // Assert
        verify(userWorkoutRepository).deleteAll(userWorkouts);
        verify(workoutRepository).deleteAll(workouts);
        verify(workoutRoomRepository).deleteById(roomId);
    }

    @Test
    void testDeleteWorkoutRoom_NotFound() {
        // Arrange
        Long roomId = 1L;

        when(workoutRoomRepository.findWorkoutRoomById(roomId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(WorkoutRoomNotFoundException.class, () ->
                workoutRoomService.deleteWorkoutRoom(roomId));
        assertEquals("No workout room found for ID: " + roomId, exception.getMessage());
    }
}
