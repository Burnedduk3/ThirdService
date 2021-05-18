package com.thirdservice.thirdservice.models.pojo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString


public class TaskPojo extends AuditAttributesEntityPojo {

    public TaskPojo(Object[] columns) {
        this.id = (Integer) columns[0];
        this.name = (String) columns[1];
        this.joinerInChargeId = (Integer) columns[2];
        this.description = (String) columns[3];
        this.estimatedRequiredHours = (Integer) columns[4];
        this.completed = (Boolean) columns[5];
        this.stack = (String) columns[6];
        this.joinerInCharge = null;
    }

    private Integer id;

    private String name;

    private String description;

    private boolean completed;

    private Integer estimatedRequiredHours;

    private String stack;

    private Integer parentTask;

    private Integer joinerInChargeId;

    private JoinerPojo joinerInCharge;
}
