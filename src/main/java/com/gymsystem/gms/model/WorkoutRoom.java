package com.gymsystem.gms.model;

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
@Table(name = "workout_room")
public class WorkoutRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String workoutName;
    private String trainerUsername;
    private String roomNumber;
    private LocalDateTime workoutStartDate;
    private LocalDateTime workoutEndDate;
    private Integer capacity;
    private Integer participantsNumber;
//    @JoinColumn(name = "fitnessClubId", referencedColumnName = "id")
//    @ManyToOne(optional = false,fetch = FetchType.EAGER)
//    private FitnessClub fitnessClub;

}
