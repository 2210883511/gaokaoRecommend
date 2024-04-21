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
public class UniversityTags {


    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer schoolId;
    private Integer provinceId;
    private Integer f985;
    private Integer f211;
    private String schoolTypeName;
    private String schoolNatureName;
    private String dualClassName;
    private String typeName;  // 综合类大学

    @TableLogic(delval = "2",value = "1")
    private Integer status;



}
