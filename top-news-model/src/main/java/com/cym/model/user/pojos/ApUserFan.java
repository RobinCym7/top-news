package com.cym.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ap_user_fan")
public class ApUserFan {
    private static final long serialVersionUID= 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "fans_id")
    private Integer fansId;

    @TableField(value = "fans_name")
    private String fansName;

    @TableField(value = "level")
    private Integer level;

    @TableField(value = "created_time")
    private Date createdTime;

    @TableField(value = "is_display")
    private Boolean isDisplay;

    @TableField(value = "is_shield_letter")
    private Boolean isShieldLetter;

    @TableField(value = "is_shield_comment")
    private Boolean isShieldComment;

}
