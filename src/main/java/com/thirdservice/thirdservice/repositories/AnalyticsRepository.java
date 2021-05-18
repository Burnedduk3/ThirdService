package com.thirdservice.thirdservice.repositories;

import com.thirdservice.thirdservice.models.pojo.DaysLeftToCompleteTaskPojo;
import com.thirdservice.thirdservice.models.pojo.JoinerPojo;
import com.thirdservice.thirdservice.models.pojo.TaskPojo;

import java.util.List;
import java.util.Map;

public interface AnalyticsRepository {
    List<JoinerPojo> topJoinersByStackOrderedByTaskCompleted(Integer numberOfJoiners, String stack);

    Map<String,List<TaskPojo>> getAllFinishedAndUnfinishedTasks();

    Map<String,List<TaskPojo>> getAllFinishedAndUnfinishedTasksForSingleJoiner(Integer joinerId);

    List<DaysLeftToCompleteTaskPojo> calculateDaysLeftToFinishTasks(List<JoinerPojo> joinersList);
}
