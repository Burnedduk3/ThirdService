package com.thirdservice.thirdservice.services;

import com.github.javafaker.Faker;
import com.thirdservice.thirdservice.models.entities.Role;
import com.thirdservice.thirdservice.repositories.RoleRepository;
import com.thirdservice.thirdservice.repositories.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.postgresql.hostchooser.HostRequirement.any;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestRoleService {
    private Faker fakeValuesService;

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void init(){
        fakeValuesService = new Faker();
    }


    @Test
    public void getRoleById(){
        Role role = new Role();
        role.setId(fakeValuesService.number().randomDigit());
        role.setName(fakeValuesService.commerce().department());

        when(roleRepository.getOne(anyInt())).thenReturn(role);

        Role foundRole = roleService.getRoleById(fakeValuesService.number().randomDigit());

        verify(roleRepository).getOne(anyInt());

        assertNotNull(foundRole);
    }

    @Test
    public void existsById(){
        when(roleRepository.existsById(anyInt())).thenReturn(Boolean.TRUE);
        int idToVerify = fakeValuesService.number().randomDigit();
        boolean result = roleService.getRoleByIdReturnId(idToVerify);

        verify(roleRepository).existsById(idToVerify);

        assertTrue(result);
    }

    @Test
    public void dontExistsById(){
        when(roleRepository.existsById(anyInt())).thenReturn(Boolean.TRUE);
        int idToVerify = fakeValuesService.number().randomDigit();
        boolean result = roleService.getRoleByIdReturnId(idToVerify);

        verify(roleRepository).existsById(idToVerify);

        assertTrue(result);
    }

    @Test
    public void createRole(){
        Role testRole = new Role();
        testRole.setName(fakeValuesService.commerce().department());
        testRole.setId(fakeValuesService.idNumber().hashCode());

        when(roleRepository.saveAndFlush(any())).thenReturn(testRole);

        roleService.createRole(testRole);

        verify(roleRepository).saveAndFlush(any());
    }

    @Test
    public void listRole(){
        List<Role> roleList = new ArrayList<>();

        for (int i = 0; i < fakeValuesService.number().numberBetween(10,20); i++) {
            Role testRole = new Role();
            testRole.setName(fakeValuesService.commerce().department());
            testRole.setId(fakeValuesService.idNumber().hashCode());

            roleList.add(testRole);
        }

        when(roleRepository.findAll()).thenReturn(roleList);

        roleList = roleService.listRoles();

        verify(roleRepository).findAll();

        assertTrue(roleList.size() > 0);
    }

    @Test
    public void deleteRole(){
        doNothing().when(roleRepository).deleteById(anyInt());

        roleService.deleteRoleById(fakeValuesService.number().randomDigit());

        verify(roleRepository).deleteById(anyInt());
    }
}
