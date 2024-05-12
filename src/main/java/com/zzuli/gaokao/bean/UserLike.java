package com.zzuli.gaokao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLike {

    private Integer id;

    private Integer userId;

    private Integer entityType;

    private Integer entityId;

    private Integer recommend;

    private Long createTime;

}
