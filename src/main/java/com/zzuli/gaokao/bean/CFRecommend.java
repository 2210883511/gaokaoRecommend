package com.zzuli.gaokao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CFRecommend {

    private FileDataModel model;

    private SVDRecommender recommender;

}
