package com.cym.model.behavior.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CollectionBehaviorDto {

    private Long entryId;
    private Short operation;
    private Date publishTime;
    private Short type;

}
