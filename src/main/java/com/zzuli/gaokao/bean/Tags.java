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
public class Tags {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer type;
    @TableLogic(delval = "2",value = "1")
    private Integer status;
}
