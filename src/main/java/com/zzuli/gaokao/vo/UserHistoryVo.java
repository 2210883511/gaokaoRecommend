package com.zzuli.gaokao.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryVo {

    private Integer userId;

    private Integer schoolId;

    private String headerUrl;

    private String schoolName;

    private Date createTime;

}
