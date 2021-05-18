package com.thirdservice.thirdservice.controllers;

import com.thirdservice.thirdservice.models.pojo.DaysLeftToCompleteTaskPojo;
import com.thirdservice.thirdservice.models.pojo.JoinerPojo;
import com.thirdservice.thirdservice.models.pojo.TaskPojo;
import com.thirdservice.thirdservice.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/top-joiners-by-stack")
    public List<JoinerPojo> topJoinersByStackOrderedByTaskCompletedController (
            @RequestParam Integer numberOfJoiners,
            @RequestParam String stack
    ){
        return analyticsService.topJoinersByStackOrderedByTaskCompletedService(numberOfJoiners, stack);
    }

    @GetMapping("/task-report")
    public Map<String,List<TaskPojo>> taskReport() {
        return analyticsService.getAllFinishedAndUnfinishedTasksService();
    }

    @GetMapping("/task-report/{joinerId}")
    public Map<String,List<TaskPojo>> taskReport(@PathVariable Integer joinerId) {
        return analyticsService.getAllFinishedAndUnfinishedTasksForJoinerService(joinerId);
    }

    @GetMapping("/time-to-complete")
    public List<DaysLeftToCompleteTaskPojo> getTimeReport() {
        return analyticsService.getTimeLeftForTasks();
    }
}
