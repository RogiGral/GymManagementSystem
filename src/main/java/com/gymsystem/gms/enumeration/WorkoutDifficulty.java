package com.gymsystem.gms.enumeration;

import static com.gymsystem.gms.constraints.WorkoutDifficulty.*;

public enum WorkoutDifficulty {
    BEGINNER(DIFFICULTY_BEGINNER),
    EASY(DIFFICULTY_EASY),
    MEDIUM(DIFFICULTY_MEDIUM),
    HARD(DIFFICULTY_HARD),
    VERY_HARD(DIFFICULTY_VERY_HARD);

    private String workoutDifficulty;

    WorkoutDifficulty(String workoutDifficulty) {
        this.workoutDifficulty = workoutDifficulty;
    }

    public String getWorkoutDifficulty() {
        return workoutDifficulty;
    }
}