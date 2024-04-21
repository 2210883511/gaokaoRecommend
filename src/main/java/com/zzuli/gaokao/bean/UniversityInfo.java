package com.zzuli.gaokao.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniversityInfo {


    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer schoolId;
    private String content;
    private Date addTime;
    private Date updateTime;
    private String createDate;
    private String area;
    private String belong;
    private String address;
    private Integer numSubject;
    private Integer numDoctor;
    private Integer numMaster;
    private Integer status;

}
