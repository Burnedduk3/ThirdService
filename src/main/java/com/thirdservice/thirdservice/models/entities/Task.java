package com.thirdservice.thirdservice.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "tasks")
public class Task extends AuditAttributesEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "description", nullable = false, length = 510)
    @NotNull
    private String description;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "estimated_time", nullable = false)
    @NotNull
    private Integer estimatedRequiredHours;

    @Column(name = "stack", nullable = false)
    @NotNull
    private String stack;

    @Column(name = "parent_task")
    private Integer parentTask = null;

    @Column(name = "joiner_in_charge")
    private Integer joinerInCharge;

    @ManyToMany
    @JoinTable(
            name = "task_role_relationship",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> minRoles;
}
