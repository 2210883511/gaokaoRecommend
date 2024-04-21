package com.zzuli.gaokao.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniversityMaster {


    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer schoolId;


    private String xuekeRankScore;


    private Integer specialId;

    private Integer nationFeature;



    private Integer provinceFeature;



    private String specialName;


    @TableLogic(delval = "2",value = "1")
    private Integer status;



}
