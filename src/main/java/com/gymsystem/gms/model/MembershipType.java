package com.gymsystem.gms.model;

import com.gymsystem.gms.enumeration.UnitOfTime;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "membership_type")
public class MembershipType {

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBERSHIP_TYPE_SEQ")
    @SequenceGenerator(name = "MEMBERSHIP_TYPE_SEQ", sequenceName = "MEMBERSHIP_TYPE_SEQ", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String membershipProductId;
    private String name;
    private String description;
    private String type;
    private Long price;
    private Integer validityPeriodNumber;
    private UnitOfTime validityUnitOfTime;

}
