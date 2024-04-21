package com.zzuli.gaokao.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MasterDetailVo {
    private Integer id;
    private Integer typeDetailId;
    private Integer detailId;
    private String name;
    private String job;
    private String isWhat;
    private String doWhat;
    private String course;
    private String learnWhat;
    private String content;

}
