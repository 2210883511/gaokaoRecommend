package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UniversityProfile {


    @TableId
    private Integer schoolId;

    private String schoolName;

    private String tags;
}
