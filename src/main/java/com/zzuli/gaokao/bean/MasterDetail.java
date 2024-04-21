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
public class MasterDetail {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer masterId;

    private String job;

    private String isWhat;

    private String doWhat;

    private String course;

    private String learnWhat;

    private String content;


}
