package com.thirdservice.thirdservice.services;

import com.github.javafaker.Faker;
import com.thirdservice.thirdservice.models.entities.Task;
import com.thirdservice.thirdservice.repositories.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestTaskService {

    @Value("${second.service.endpoint}")
    private String secondServiceUri;

    private Faker fakeValuesService;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void init(){
        fakeValuesService = new Faker();
    }

    @Test
    public void getTaskById(){
        Task testTask = new Task();
        testTask.setCompleted(false);
        testTask.setDescription(fakeValuesService.lorem().characters(50));
        testTask.setId(fakeValuesService.number().numberBetween(0,10));
        testTask.setName(fakeValuesService.commerce().productName());
        when(taskRepository.getOne(anyInt())).thenReturn(testTask);

        Task processedTask = taskService.getTaskById(testTask.getId());

        verify(taskRepository).getOne(testTask.getId());

        assertEquals(testTask.getId(), processedTask.getId());
        assertEquals(testTask.getDescription(), processedTask.getDescription());
    }

    @Test
    public void getAllTasks(){
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < fakeValuesService.number().numberBetween(10,20); i++) {
            Task testTask = new Task();
            testTask.setCompleted(fakeValuesService.bool().bool());
            testTask.setDescription(fakeValuesService.lorem().characters(50));
            testTask.setId(fakeValuesService.number().numberBetween(0,10));
            testTask.setName(fakeValuesService.commerce().productName());
            taskList.add(testTask);
        }

        when(taskRepository.findAll()).thenReturn(taskList);

        List<Integer> tasksIds = taskService.getAllTasks();

        verify(taskRepository).findAll();

        int indexToCheck = fakeValuesService.number().numberBetween(0, taskList.size());

        assertEquals(taskList.get(indexToCheck).getId(), tasksIds.get(indexToCheck));
    }

    @Test
    public void updateTaskById(){
        Task testTask = new Task();
        testTask.setCompleted(fakeValuesService.bool().bool());
        testTask.setDescription(fakeValuesService.lorem().characters(50));
        testTask.setId(fakeValuesService.number().numberBetween(0,10));
        testTask.setName(fakeValuesService.commerce().productName());

        when(taskRepository.getOne(anyInt())).thenReturn(testTask);

        taskService.updateTaskById(testTask.getId(), testTask);

        verify(taskRepository).getOne(testTask.getId());

        verify(taskRepository).saveAndFlush(any(Task.class));
    }

    @Test
    public void partiallyUpdateTaskByIdSuccess(){
        Task testTask = new Task();
        testTask.setCompleted(fakeValuesService.bool().bool());
        testTask.setDescription(fakeValuesService.lorem().characters(50));
        testTask.setId(fakeValuesService.number().numberBetween(0,10));
        testTask.setName(fakeValuesService.commerce().productName());

        when(taskRepository.getOne(anyInt())).thenReturn(testTask);

        taskService.partiallyUpdateTaskById(testTask.getId(), new Task());

        verify(taskRepository).getOne(testTask.getId());
        verify(taskRepository).saveAndFlush(any(Task.class));
    }

    @Test
    public void deleteTask(){
        doNothing().when(taskRepository).deleteById(anyInt());

        taskService.deleteTaskById(fakeValuesService.number().randomDigit());

        verify(taskRepository).deleteById(anyInt());
    }

    @Test
    public void createTaskSuccessfully(){
        Task testTask = new Task();
        testTask.setCompleted(fakeValuesService.bool().bool());
        testTask.setDescription(fakeValuesService.lorem().characters(50));
        testTask.setId(fakeValuesService.number().numberBetween(0,10));
        testTask.setJoinerInCharge(fakeValuesService.number().numberBetween(0,10));
        testTask.setName(fakeValuesService.commerce().productName());

        String uri = secondServiceUri + "/exists/%s";
        uri = String.format(uri, testTask.getJoinerInCharge().toString());

        when(restTemplate.getForObject(uri, Boolean.class)).thenReturn(Boolean.TRUE);

        when(taskRepository.saveAndFlush(any())).thenReturn(testTask);

        taskService.createTask(testTask);

        verify(taskRepository).saveAndFlush(testTask);
        verify(restTemplate).getForObject(uri, Boolean.class);
    }

    @Test(expected = ResponseStatusException.class)
    public void createTaskUnsuccessfully(){
        Task testTask = new Task();
        testTask.setCompleted(fakeValuesService.bool().bool());
        testTask.setDescription(fakeValuesService.lorem().characters(50));
        testTask.setId(fakeValuesService.number().numberBetween(0,10));
        testTask.setJoinerInCharge(fakeValuesService.number().numberBetween(0,10));
        testTask.setName(fakeValuesService.commerce().productName());

        String uri = secondServiceUri + "/exists/%s";
        uri = String.format(uri, testTask.getJoinerInCharge().toString());
        when(restTemplate.getForObject(uri, Boolean.class)).thenReturn(Boolean.FALSE);

        taskService.createTask(testTask);

        verify(restTemplate).getForObject(uri, Boolean.class);
    }
}
