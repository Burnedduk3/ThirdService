package com.thirdservice.thirdservice.models.pojo;

import lombok.*;

import java.math.BigInteger;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class DaysLeftToCompleteTaskPojo {

    private Integer joinerId;

    private BigInteger days;

    private BigInteger hours;
}
