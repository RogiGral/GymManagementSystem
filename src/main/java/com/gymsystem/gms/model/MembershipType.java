package com.gymsystem.gms.model;

import com.gymsystem.gms.enumeration.UnitOfTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "membership_type")
public class MembershipType {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBERSHIP_TYPE_SEQ")
    @SequenceGenerator(name = "MEMBERSHIP_TYPE_SEQ", sequenceName = "MEMBERSHIP_TYPE_SEQ", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long price;
    private Integer validityPeriodNumber;
    private UnitOfTime validityUnitOfTime;



}
