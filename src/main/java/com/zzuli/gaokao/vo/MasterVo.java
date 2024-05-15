package com.zzuli.gaokao.vo;


import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.MasterDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterVo {

    private Integer id;
    private String name;
    private String code;
    private String limitYear;
    private String type;
    private String typeDetail;
    private String levelName;
    private String degree;

    private String job;
    private String isWhat;
    private String doWhat;
    private String course;

    private String learnWhat;

    private String content;

    public void setMaster(Master master){
        this.id = master.getId();
        this.name = master.getName();
        this.limitYear = master.getLimitYear();
        this.code = master.getCode();
        this.type = master.getType();
        this.typeDetail = master.getTypeDetail();
        this.degree = master.getDegree();
        this.levelName = master.getLevel1Name();
    }

    public void setDetail(MasterDetail masterDetail){
        this.job = masterDetail.getJob();
        this.isWhat = masterDetail.getIsWhat();
        this.doWhat = masterDetail.getDoWhat();
        this.learnWhat = masterDetail.getLearnWhat();
        this.course = masterDetail.getCourse();
        this.content = masterDetail.getContent();
    }

}
