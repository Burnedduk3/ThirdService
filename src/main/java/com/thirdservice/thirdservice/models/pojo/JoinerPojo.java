package com.thirdservice.thirdservice.models.pojo;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString


public class JoinerPojo {

    public JoinerPojo(Object[] columns) {
        this.id = (Integer) columns[0];
        this.identificationNumber = (Integer) columns[1];
        this.name = (String) columns[2];
        this.lastname = (String) columns[3];
        this.englishLevel = (String) columns[4];
        this.domainExperience = (String) columns[5];
        this.roleId = (Integer) columns[6];
        this.tasksCompleted = (BigInteger) columns[7];
        this.role = null;
    }

    public JoinerPojo(
            Integer id,
            Integer identificationNumber,
            String name,
            String lastname,
            String englishLevel,
            Integer role,
            String domainExperience
    ) {
        this.id = id;
        this.identificationNumber = identificationNumber;
        this.name = name;
        this.lastname = lastname;
        this.englishLevel = englishLevel;
        this.domainExperience = domainExperience;
        this.role = null;
        this.tasksCompleted = null;
        this.roleId = role;
    }

    private Integer id;

    private Integer identificationNumber;

    private String name;

    private String lastname;

    private String englishLevel;

    private String domainExperience;

    private RolePojo role;

    private BigInteger tasksCompleted;

    private Integer roleId;
}
