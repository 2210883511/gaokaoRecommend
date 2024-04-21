package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Master {


    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer typeDetailId;
    private String name;
    private String code;

    private String limitYear;
    private String type;
    private String typeDetail;
    private String level1Name;
    private String degree;
    private Date addTime;
    private Date updateTime;
    @TableLogic(delval = "2",value = "1")
    private Integer status;

}
