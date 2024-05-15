package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAction {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer entityType;

    private Integer entityId;

    private Long createTime;

}
