package com.thirdservice.thirdservice.repositories;

import com.thirdservice.thirdservice.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByNameEquals(String name);
}
