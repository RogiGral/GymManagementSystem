package com.gymsystem.gms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_membership")
public class UserMembership {
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_MEMBERSHIP_SEQ")
    @SequenceGenerator(name = "USER_MEMBERSHIP_SEQ", sequenceName = "USER_MEMBERSHIP_SEQ", allocationSize = 1)
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
