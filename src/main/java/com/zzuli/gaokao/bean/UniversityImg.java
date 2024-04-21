package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class UniversityImg {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer schoolId;
    private Integer rank;
    private String url;
    private String title;

    @TableLogic(delval = "2", value = "1")
    private Integer status;
}
