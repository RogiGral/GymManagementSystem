package com.gymsystem.gms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "score")
public class Score {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SCORE_SEQ")
    @SequenceGenerator(name = "SCORE_SEQ", sequenceName = "SCORE_SEQ", allocationSize = 1)
    @Column(insertable = false, updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private BigDecimal value;
}
