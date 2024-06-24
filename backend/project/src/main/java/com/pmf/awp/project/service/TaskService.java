package com.pmf.awp.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.Task;
import com.pmf.awp.project.repository.TaskRepository;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Task findById(Integer taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new GenericHttpException("Task not found", HttpStatus.NOT_FOUND));
    }

    public Task save(Task task) {
        try {
            return taskRepository.save(task);
        } catch (Exception e) {
            throw new GenericHttpException("Unable to create/save task", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean remove(Task task) {
        try {
            taskRepository.delete(task);
            return true;
        } catch (Exception e) {
            throw new GenericHttpException("Unable to delete task", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
