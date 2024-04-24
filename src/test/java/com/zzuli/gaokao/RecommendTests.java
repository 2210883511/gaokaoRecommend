package com.zzuli.gaokao;


import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorization;

import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.vectorizer.TFIDF;
import org.apache.mahout.vectorizer.tfidf.TFIDFConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;



import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class RecommendTests {



    @Test
    public void alsTest() throws TasteException, IOException {

        ClassPathResource resource = new ClassPathResource("/test.txt");
        File file = resource.getFile();
        FileDataModel model = new FileDataModel(file);

        ALSWRFactorizer factorizer = new ALSWRFactorizer(model,5,0.001,100);
        Factorization factorize = factorizer.factorize();
        for (double[] doubles : factorize.allItemFeatures()) {
            for (double aDouble : doubles) {
                System.out.print(aDouble + " ");
            }
            System.out.println();
        }
        LongPrimitiveIterator userIDs = model.getUserIDs();
        SVDRecommender recommender = new SVDRecommender(model,factorizer);
        while (userIDs.hasNext()){
            Long id = userIDs.next();
            List<RecommendedItem> recommend = recommender.recommend(id, 20);
            System.out.println("给用户:" + id+ "推荐的大学有：");
            for (RecommendedItem recommendedItem : recommend) {
                System.out.println(recommendedItem.getItemID() + "  " +recommendedItem.getValue());
            }

        }



    }

}
