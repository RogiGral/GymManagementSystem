package com.gymsystem.gms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_workout")
public class UserWorkout {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_WORKOUT_SEQ")
    @SequenceGenerator(name = "USER_WORKOUT_SEQ", sequenceName = "USER_WORKOUT_SEQ", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    @JoinColumn(name = "userId", referencedColumnName = "id")
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private User user;

    @JoinColumn(name = "workoutId", referencedColumnName = "id")
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private Workout workout;
}
