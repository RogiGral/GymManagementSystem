package com.gymsystem.gms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
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
