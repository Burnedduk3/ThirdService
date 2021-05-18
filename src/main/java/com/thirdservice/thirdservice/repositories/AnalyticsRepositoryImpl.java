package com.thirdservice.thirdservice.repositories;

import com.thirdservice.thirdservice.models.pojo.DaysLeftToCompleteTaskPojo;
import com.thirdservice.thirdservice.models.pojo.JoinerPojo;
import com.thirdservice.thirdservice.models.pojo.TaskPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AnalyticsRepositoryImpl implements AnalyticsRepository{

    @PersistenceContext
    private EntityManager entityManager;

    public List<JoinerPojo> topJoinersByStackOrderedByTaskCompleted(Integer numberOfJoiners, String stack){
        List<JoinerPojo> orderedJoinersList = new ArrayList<>();
        Query getJoinersByStackQuery = entityManager.createNativeQuery("SELECT " +
                "new_joiners.id," +
                "new_joiners.identification_number," +
                "new_joiners.name," +
                "new_joiners.lastname, " +
                "new_joiners.english_level, " +
                "new_joiners.domain_experience, " +
                "new_joiners.role_id, " +
                "count(tasks.completed) as tasks_completed " +
                "FROM new_joiners " +
                "LEFT JOIN tasks " +
                "ON new_joiners.id = tasks.joiner_in_charge " +
                "WHERE tasks.stack = ?1 AND tasks.completed " +
                "GROUP BY " +
                "new_joiners.id, " +
                "tasks.completed " +
                "ORDER BY tasks_completed desc " +
                "limit ?2"
        );

        getJoinersByStackQuery.setParameter(2,numberOfJoiners);
        getJoinersByStackQuery.setParameter(1, stack);
        var joinersList = getJoinersByStackQuery.getResultList();

        joinersList.forEach(joiner -> orderedJoinersList.add((new JoinerPojo((Object[]) joiner))));

        return orderedJoinersList;
    }

    public Map<String,List<TaskPojo>> getAllFinishedAndUnfinishedTasks() {
        Map<String, List<TaskPojo>> taskResults = new HashMap<>();

        List<TaskPojo> finishedTasks = new ArrayList<>();
        List<TaskPojo> unfinishedTasks = new ArrayList<>();

        Query getFinishedTasks = entityManager.createNativeQuery("SELECT  " +
                "tasks.id,  " +
                "tasks.name,  " +
                "tasks.joiner_in_charge,  " +
                "tasks.description,  " +
                "tasks.estimated_time, " +
                "tasks.completed, " +
                "tasks.stack " +
                "FROM tasks " +
                "WHERE tasks.completed");

        var databaseFinishedTasks = getFinishedTasks.getResultList();

        databaseFinishedTasks.forEach(task -> finishedTasks.add(new TaskPojo((Object[]) task)));


        Query getUnfinishedTasks = entityManager.createNativeQuery("SELECT  " +
                "tasks.id,  " +
                "tasks.name,  " +
                "tasks.joiner_in_charge,  " +
                "tasks.description,  " +
                "tasks.estimated_time, " +
                "tasks.completed, " +
                "tasks.stack " +
                "FROM tasks " +
                "WHERE NOT tasks.completed"
        );

        var databaseUnfinishedTasks = getUnfinishedTasks.getResultList();

        databaseUnfinishedTasks.forEach(task -> unfinishedTasks.add(new TaskPojo((Object[]) task)));

        taskResults.put("finished",finishedTasks);
        taskResults.put("unfinished",unfinishedTasks);

        return taskResults;
    }

    public Map<String,List<TaskPojo>> getAllFinishedAndUnfinishedTasksForSingleJoiner(Integer joinerId) {
        Map<String, List<TaskPojo>> taskResults = new HashMap<>();

        List<TaskPojo> finishedTasks = new ArrayList<>();
        List<TaskPojo> unfinishedTasks = new ArrayList<>();

        Query getFinishedTasksForJoiner = entityManager.createNativeQuery("SELECT " +
                "tasks.id, " +
                "tasks.name,  " +
                "tasks.joiner_in_charge,  " +
                "tasks.description,  " +
                "tasks.estimated_time, " +
                "tasks.completed, " +
                "tasks.stack " +
                "FROM tasks " +
                "LEFT JOIN new_joiners ON tasks.joiner_in_charge = new_joiners.id " +
                "WHERE tasks.completed AND new_joiners.id = ?1 "
        );

        getFinishedTasksForJoiner.setParameter(1, joinerId);

        var databaseFinishedTasks = getFinishedTasksForJoiner.getResultList();

        databaseFinishedTasks.forEach(task -> finishedTasks.add(new TaskPojo((Object[]) task)));

        Query getUnfinishedTasksForJoiner = entityManager.createNativeQuery("SELECT " +
                "tasks.id, " +
                "tasks.name,  " +
                "tasks.joiner_in_charge,  " +
                "tasks.description,  " +
                "tasks.estimated_time, " +
                "tasks.completed, " +
                "tasks.stack " +
                "FROM tasks " +
                "LEFT JOIN new_joiners ON tasks.joiner_in_charge = new_joiners.id " +
                "WHERE NOT tasks.completed AND new_joiners.id = ?1"
        );

        getUnfinishedTasksForJoiner.setParameter(1,joinerId);

        var databaseUnfinishedTasks = getUnfinishedTasksForJoiner.getResultList();

        databaseUnfinishedTasks.forEach(task -> unfinishedTasks.add(new TaskPojo((Object[]) task)));

        taskResults.put("finished",finishedTasks);
        taskResults.put("unfinished",unfinishedTasks);

        return taskResults;
    }

    public List<DaysLeftToCompleteTaskPojo> calculateDaysLeftToFinishTasks(List<JoinerPojo> joinersList){
        List<DaysLeftToCompleteTaskPojo> taskResults = new ArrayList<>();

        joinersList.forEach(joiner -> {
            List<TaskPojo> unfinishedTasks = new ArrayList<>();
            Query getUnfinishedTasksForJoinerCalculateHours = entityManager.createNativeQuery("SELECT " +
                    "tasks.id, " +
                    "tasks.name,  " +
                    "tasks.joiner_in_charge,  " +
                    "tasks.description,  " +
                    "tasks.estimated_time, " +
                    "tasks.completed, " +
                    "tasks.stack " +
                    "FROM tasks " +
                    "LEFT JOIN new_joiners ON tasks.joiner_in_charge = new_joiners.id " +
                    "WHERE NOT tasks.completed AND new_joiners.id = ?1"
            );

            getUnfinishedTasksForJoinerCalculateHours.setParameter(1, joiner.getId());

            var databaseUnfinishedTasks = getUnfinishedTasksForJoinerCalculateHours.getResultList();

            databaseUnfinishedTasks.forEach(task -> unfinishedTasks.add(new TaskPojo((Object[]) task)));

            if(unfinishedTasks.size() > 0){

                Query getTimeLeft = entityManager.createNativeQuery("SELECT  " +
                        "SUM(tasks.estimated_time) " +
                        "from tasks  " +
                        "where tasks.joiner_in_charge = ?1 AND NOT completed  " +
                        "GROUP BY joiner_in_charge"
                );
                getTimeLeft.setParameter(1, joiner.getId());

                BigInteger timeLeftInHours = (BigInteger) getTimeLeft.getSingleResult();
                BigInteger days  = timeLeftInHours.divide(BigInteger.valueOf(24));
                BigInteger hours = timeLeftInHours.mod(BigInteger.valueOf(24));
                taskResults.add(new DaysLeftToCompleteTaskPojo(joiner.getId(), days, hours));
            }
        });

        return taskResults;
    }

}
