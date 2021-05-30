package com.thirdservice.thirdservice.services;

import com.thirdservice.thirdservice.models.pojo.DaysLeftToCompleteTaskPojo;
import com.thirdservice.thirdservice.models.pojo.JoinerPojo;
import com.thirdservice.thirdservice.models.pojo.RolePojo;
import com.thirdservice.thirdservice.models.pojo.TaskPojo;
import com.thirdservice.thirdservice.repositories.AnalyticsRepository;
import com.thirdservice.thirdservice.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${second.service.endpoint}")
    private String secondServiceUri;

    public List<JoinerPojo> topJoinersByStackOrderedByTaskCompletedService(Integer numberOfJoiners, String stack){
        List<JoinerPojo> joiners = analyticsRepository.topJoinersByStackOrderedByTaskCompleted(numberOfJoiners, stack);
        joiners.forEach(joiner -> {
            var foundRole = roleRepository.getOne(joiner.getRoleId());
            var mappedRole = new RolePojo(foundRole.getId(), foundRole.getName(), null, foundRole.getCreatedOn());
            joiner.setRole(mappedRole);
        });
        return joiners;
    }

    public Map<String,List<TaskPojo>> getAllFinishedAndUnfinishedTasksService() {
        Map<String,List<TaskPojo>> tasks = analyticsRepository.getAllFinishedAndUnfinishedTasks();

        return getStringListMap(tasks);
    }

    public Map<String,List<TaskPojo>> getAllFinishedAndUnfinishedTasksForJoinerService(Integer joinerId) {
        Map<String,List<TaskPojo>> tasks = analyticsRepository.getAllFinishedAndUnfinishedTasksForSingleJoiner(joinerId);

        return getStringListMap(tasks);
    }

    private Map<String, List<TaskPojo>> getStringListMap(Map<String, List<TaskPojo>> tasks) {
        tasks.forEach((key, taskList) -> taskList.forEach(task -> {
            var uri = String.format(secondServiceUri + "/%s", task.getJoinerInChargeId().toString());
            var restTemplate = new RestTemplate();
            Optional<JoinerPojo> joinerExistsOptional = Optional.ofNullable(restTemplate.getForObject(uri, JoinerPojo.class));
            JoinerPojo joiner = joinerExistsOptional.orElse(new JoinerPojo());
            task.setJoinerInCharge(joiner);
        }));
        return tasks;
    }

    public List<DaysLeftToCompleteTaskPojo> getTimeLeftForTasks(){
        var uri = secondServiceUri + "/get-all";
        var restTemplate = new RestTemplate();
        ResponseEntity<List<JoinerPojo>> responseEntity = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<JoinerPojo>>() {}
                );
        List<JoinerPojo> joiners = responseEntity.getBody();

        return analyticsRepository.calculateDaysLeftToFinishTasks(joiners);
    }

}
