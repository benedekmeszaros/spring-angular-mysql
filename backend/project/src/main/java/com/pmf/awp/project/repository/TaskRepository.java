package com.pmf.awp.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pmf.awp.project.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

}
