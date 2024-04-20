package com.gymsystem.gms.model;


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
@Table(name = "fitness_club")
public class FitnessClub {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FITNESS_CLUB_SEQ")
    @SequenceGenerator(name = "FITNESS_CLUB_SEQ", sequenceName = "FITNESS_CLUB_SEQ", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private String fullAddress;

}
