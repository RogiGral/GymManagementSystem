package com.gymsystem.gms.model;

import com.gymsystem.gms.enumeration.WorkoutDifficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workout")
public class Workout {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WORKOUT_SEQ")
    @SequenceGenerator(name = "WORKOUT_SEQ", sequenceName = "WORKOUT_SEQ", allocationSize = 1)
    @Column(insertable = false, updatable = false, unique = true, nullable = false)
    private Long id;
    @Column(length = 50, nullable = false)
    private String workoutName;
    @Column(nullable = false)
    private String trainerUsername;
    @Column(length = 4, nullable = false)
    private String roomNumber;
    @Column(nullable = false)
    private LocalDateTime workoutStartDate;
    @Column(nullable = false)
    private LocalDateTime workoutEndDate;
    @Column(nullable = false)
    private Integer capacity;
    @Column(nullable = false)
    private Integer participantsNumber;
    @Column(length = 30, nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private WorkoutDifficulty workoutDifficulty;
//    @JoinColumn(name = "fitnessClubId", referencedColumnName = "id")
//    @ManyToOne(optional = false,fetch = FetchType.EAGER)
//    private FitnessClub fitnessClub;

}
