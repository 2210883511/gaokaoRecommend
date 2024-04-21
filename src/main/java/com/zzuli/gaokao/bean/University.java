package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class University {


    @TableId
    private Integer schoolId;

    private String schoolName;
    private Integer provinceId;
    private String cityName;
    private String townName;
    private String phone;
    private String site;
    private String schoolSite;
    private String email;
    private String headerUrl;
    @TableLogic(delval = "2",value = "1")
    private Integer status;

}
