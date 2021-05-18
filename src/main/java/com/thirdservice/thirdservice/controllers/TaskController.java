package com.thirdservice.thirdservice.controllers;

import com.thirdservice.thirdservice.models.entities.Task;
import com.thirdservice.thirdservice.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping
    public Task createTask(@RequestBody Task task){
        return taskService.createTask(task);
    }

    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable Integer taskId){
        return taskService.getTaskById(taskId);
    }

    @GetMapping()
    public List<Integer> getAllTasksIds(){
        return taskService.getAllTasks();
    }

    @DeleteMapping("/{taskId}")
    public void deleteTaskById(@PathVariable Integer taskId){
        taskService.deleteTaskById(taskId);
    }

    @PatchMapping("/{taskId}")
    public Task partiallyUpdateTaskById(@PathVariable Integer taskId, @RequestBody Task updatedTask){
        return taskService.partiallyUpdateTaskById(taskId, updatedTask);
    }

    @PutMapping("/{taskId}")
    public Task updateTaskById(@PathVariable Integer taskId, @RequestBody Task updatedTask){
        return taskService.updateTaskById(taskId, updatedTask);
    }
}
