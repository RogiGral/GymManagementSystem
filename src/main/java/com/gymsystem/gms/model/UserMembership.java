package com.gymsystem.gms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "user_membership")
public class UserMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, updatable = false)
    private Long id;
    @JoinColumn(name = "userId", referencedColumnName = "id")
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private User userId;

    @JoinColumn(name = "membershipTypeId", referencedColumnName = "id")
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private MembershipType membershipTypeId;

    private Date startDate;
    private Date endDate;
}
