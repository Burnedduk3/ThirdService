package com.thirdservice.thirdservice.models.pojo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class RolePojo extends AuditAttributesEntityPojo {

    private Integer id;

    private String name;

    private List<TaskPojo> associatedTasks;

    public RolePojo(Integer id, String name, List<TaskPojo> associatedTasks, LocalDateTime createdOn) {
        super(createdOn);
        this.id = id;
        this.name = name;
        this.associatedTasks = associatedTasks;
    }

}
