package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @TableId
    private Integer id;

    private String username;

    private String email;

    private Integer emailVerified;

    private String nickname;

    private String headerUrl;

    private String backgroundImage;

    private String password;

    private String description;

    private String roles;

    private Integer provinceId;

    private String salt;

    private String profile;

    @TableLogic(value = "1",delval = "2")
    private Integer status;

    private Date createTime;

    private Date updateTime;

}
