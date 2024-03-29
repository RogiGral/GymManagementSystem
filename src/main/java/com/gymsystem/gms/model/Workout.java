package com.gymsystem.gms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workout")
public class Workout {

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
