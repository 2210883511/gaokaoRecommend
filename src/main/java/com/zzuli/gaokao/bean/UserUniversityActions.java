package com.zzuli.gaokao.bean;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUniversityActions {

    @TableId
    private Integer id;

    private Integer userId;

    private Integer universityId;

    private Integer entityType;

    @TableField(exist = false)
    private Integer value;

}
