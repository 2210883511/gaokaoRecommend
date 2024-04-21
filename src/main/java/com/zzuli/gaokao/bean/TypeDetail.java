package com.zzuli.gaokao.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("master_type_detail")
public class TypeDetail {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer typeId;

    private String typeName;

    private String typeDetail;

    @TableLogic(delval = "2", value = "1")
    private Integer status;
}
