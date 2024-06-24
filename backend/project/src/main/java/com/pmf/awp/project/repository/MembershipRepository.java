package com.pmf.awp.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pmf.awp.project.model.Membership;

import jakarta.transaction.Transactional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Integer> {
    @Modifying
    @Transactional
    @Query("delete from Membership m where m.id = :memberId")
    void deleteById(Integer memberId);
}
