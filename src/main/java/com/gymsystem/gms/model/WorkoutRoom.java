package com.gymsystem.gms.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


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
