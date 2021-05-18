package com.thirdservice.thirdservice.controllers;

import com.thirdservice.thirdservice.models.entities.Role;
import com.thirdservice.thirdservice.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @PostMapping()
    public Role createRole(@RequestBody Role roleToBeCreated) {
        return roleService.createRole(roleToBeCreated);
    }

    @GetMapping("{roleId}")
    public Role getRoleById(@PathVariable Integer roleId){
        return roleService.getRoleById(roleId);
    }

    @GetMapping("/exists/{roleId}")
    public boolean roleExistsInDB(@PathVariable Integer roleId){
        return roleService.getRoleByIdReturnId(roleId);
    }
    @GetMapping()
    public List<Role> getAllRoles () {
        return roleService.listRoles();
    }

    @DeleteMapping("{roleId}")
    public void deleteRoleById (@PathVariable Integer roleId){
        roleService.deleteRoleById(roleId);
    }

    @PatchMapping("{roleId}")
    public Role updateRole(@PathVariable Integer roleId, @RequestBody Role newRole){
        return roleService.updateRoleById(roleId, newRole);
    }
}
