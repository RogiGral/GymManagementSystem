package com.gymsystem.gms.repository;


import com.gymsystem.gms.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipTypeRepository extends JpaRepository<MembershipType,Long> {
    MembershipType findMembershipTypeByNameAndPrice(String name, Long price);
    MembershipType findMembershipTypeByName(String name);
    MembershipType findMembershipTypeById(Long membershipTypeId);
}
