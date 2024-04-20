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
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WORKOUT_ROOM_SEQ")
    @SequenceGenerator(name = "WORKOUT_ROOM_SEQ", sequenceName = "WORKOUT_ROOM_SEQ", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String workoutRoomName;
    private Integer capacity;
}
