package com.thirdservice.thirdservice.services;

import com.thirdservice.thirdservice.models.entities.Role;
import com.thirdservice.thirdservice.models.entities.Task;
import com.thirdservice.thirdservice.repositories.RoleRepository;
import com.thirdservice.thirdservice.repositories.TaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Value("${second.service.endpoint}")
    private String secondServiceUri;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RoleRepository roleRepository;

    private boolean isParentTaskInDB(Integer parentTask){
        Optional<Integer> parentTaskIdOptional = Optional.ofNullable(parentTask);
        return parentTaskIdOptional.isPresent() && !taskRepository.existsById(parentTaskIdOptional.get());
    }

    private ArrayList<Role> getDatabaseRoles(List<Role> requestRoles){
        ArrayList<Role> foundRoles = new ArrayList<>();
        Optional<List<Role>> relatedRolesOptional = Optional.ofNullable(requestRoles);

        if(relatedRolesOptional.isPresent()){
            for (Role role : relatedRolesOptional.get()){
                Optional<Role> foundedRoleOptional = Optional.ofNullable(roleRepository.findByNameEquals(role.getName()));
                if (foundedRoleOptional.isPresent()){
                    foundRoles.add(foundedRoleOptional.get());
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of the roles is not founded in the database");
                }
            }
        }
        return foundRoles;
    }

    public Task createTask(Task task){
        List<Role> foundRoles = getDatabaseRoles(task.getMinRoles());
        String uri = secondServiceUri + "/exists/%s";
        var restTemplate = new RestTemplate();
        uri = String.format(uri, task.getJoinerInCharge().toString());
        Optional<Boolean> joinerExistsOptional = Optional.ofNullable(restTemplate.getForObject(uri, Boolean.class));
        boolean joinerExists = joinerExistsOptional.orElse(false);
        if (!joinerExists){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The Joiner does not exist");
        }

        if(isParentTaskInDB(task.getParentTask())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The parent task does not exist");
        }

        task.setMinRoles(foundRoles);

        task.setCreatedOn(LocalDateTime.now());

        return taskRepository.saveAndFlush(task);
    }

    public Task getTaskById(Integer taskId){
        return taskRepository.getOne(taskId);
    }

    public List<Integer> getAllTasks(){
        List<Integer> taskIds = new ArrayList<>();
        List<Task> tasks =  taskRepository.findAll();
        tasks.forEach(task -> taskIds.add(task.getId()));
        return taskIds;
    }

    public void deleteTaskById(Integer taskId){
        taskRepository.deleteById(taskId);
    }

    public Task partiallyUpdateTaskById(Integer taskId, Task updatedTask){
        var oldTask = taskRepository.getOne(taskId);
        List<Role> foundRoles = getDatabaseRoles(updatedTask.getMinRoles());
        Optional<String> nameOptional = Optional.ofNullable(updatedTask.getName());
        Optional<String> descriptionOptional = Optional.ofNullable(updatedTask.getDescription());
        Optional<String> stackOptional = Optional.ofNullable(updatedTask.getStack());
        Optional<Integer> estimatedRequiredHoursOptional = Optional.ofNullable(updatedTask.getEstimatedRequiredHours());
        Optional<Integer> parentTaskOptional = Optional.ofNullable(updatedTask.getParentTask());

        if(isParentTaskInDB(updatedTask.getParentTask())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The parent task does not exist");
        }

        String actualName = nameOptional.orElse(oldTask.getName());
        String actualDescription = descriptionOptional.orElse(oldTask.getDescription());
        String actualStack = stackOptional.orElse(oldTask.getStack());
        Integer actualEstimatedHours = estimatedRequiredHoursOptional.orElse(oldTask.getEstimatedRequiredHours());
        Integer actualParentId = parentTaskOptional.orElse(oldTask.getParentTask());

        updatedTask.setParentTask(actualParentId);
        updatedTask.setDescription(actualDescription);
        updatedTask.setId(oldTask.getId());
        updatedTask.setStack(actualStack);
        updatedTask.setEstimatedRequiredHours(actualEstimatedHours);
        updatedTask.setName(actualName);
        updatedTask.setMinRoles(foundRoles);

        return taskRepository.saveAndFlush(updatedTask);
    }

    public Task updateTaskById(Integer taskId, Task updatedTask){
        var oldTask = taskRepository.getOne(taskId);
        List<Role> foundRoles = getDatabaseRoles(updatedTask.getMinRoles());
        updatedTask.setMinRoles(foundRoles);
        BeanUtils.copyProperties(updatedTask, oldTask, "id");
        return taskRepository.saveAndFlush(updatedTask);
    }
}
