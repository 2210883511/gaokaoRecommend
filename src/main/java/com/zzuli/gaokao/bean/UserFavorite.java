package com.zzuli.gaokao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFavorite {
    private Integer id;

    private Integer userId;

    private Integer entityType;

    private Integer entityId;

    private Long createTime;
}
