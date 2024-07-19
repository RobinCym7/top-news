package com.cym.model.user.pojos;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ap_user_follow")
public class ApUserFollow {

    private static final long serialVersionUID= 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "user_id")
    private Integer userId;

    @TableField(value = "follow_id")
    private Long followId;

    @TableField(value = "follow_name")
    private String followName;

    @TableField(value = "level")
    private Integer level;

    @TableField(value = "is_notice")
    private Boolean isNotice;

    @TableField(value = "created_time")
    private Date createTime;
}
