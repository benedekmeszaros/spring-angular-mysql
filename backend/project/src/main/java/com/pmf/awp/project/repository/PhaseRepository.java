package com.pmf.awp.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pmf.awp.project.model.Phase;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Integer> {
    @Query("select exists (select p from Phase p inner join p.tasks t where t.phase.id = :phaseId and t.label like :label)")
    boolean existingTaskWithLabel(Integer phaseId, String label);
}
