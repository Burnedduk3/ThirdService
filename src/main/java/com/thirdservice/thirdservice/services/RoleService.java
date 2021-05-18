package com.thirdservice.thirdservice.services;

import com.thirdservice.thirdservice.models.entities.Role;
import com.thirdservice.thirdservice.models.entities.Task;
import com.thirdservice.thirdservice.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role createRole(Role role){
        Optional<List<Task>> roleTasksOption = Optional.ofNullable(role.getAssociatedTasks());
        List<Task> initializedList = roleTasksOption.orElse(new ArrayList<>());
        role.setAssociatedTasks(initializedList);
        role.setCreatedOn(LocalDateTime.now());
        return roleRepository.saveAndFlush(role);
    }

    public Role getRoleById(Integer roleId){
        return roleRepository.getOne(roleId);
    }

    public boolean getRoleByIdReturnId(Integer roleId){
        return roleRepository.existsById(roleId);
    }

    public List<Role> listRoles(){
        return roleRepository.findAll();
    }

    public void deleteRoleById(Integer roleId){
        roleRepository.deleteById(roleId);
    }

    public Role updateRoleById(Integer roleId, Role newRole){
        Role oldRole = roleRepository.getOne(roleId);
        Optional<List<Task>> roleTasksOption = Optional.ofNullable(newRole.getAssociatedTasks());
        Optional<String> roleNameOptions = Optional.ofNullable(newRole.getName());
        newRole.setAssociatedTasks(roleTasksOption.orElse(oldRole.getAssociatedTasks()));
        newRole.setName(roleNameOptions.orElse(oldRole.getName()));
        newRole.setId(oldRole.getId());
        return roleRepository.saveAndFlush(newRole);
    }
}
