package com.thirdservice.thirdservice.repositories;

import com.thirdservice.thirdservice.models.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> { }
