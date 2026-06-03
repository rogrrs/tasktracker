package com.example.tasktracker.repository;

import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"owner", "assignee"})
    List<Task> findAllByOwnerOrAssignee(User owner, User assignee);

    @EntityGraph(attributePaths = {"owner", "assignee"})
    List<Task> findAll();
}