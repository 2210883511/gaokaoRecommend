package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterType {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String type;

    private String levelName;

    @TableLogic(delval = "2", value = "1")
    private Integer status;


}
