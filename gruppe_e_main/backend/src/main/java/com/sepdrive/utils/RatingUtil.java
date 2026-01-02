package com.sepdrive.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RatingUtil {

    public static double computeRating(List<Integer> ratings, int newRating){

        if (ratings == null || ratings.isEmpty()){
            return newRating;
        };

        int total = 0;

        for (Integer rating : ratings){
            if (rating != null){
                total += rating;
            }
        }

        total += newRating;
        int count = ratings.size() + 1;
        double avg = (double) total / count;

        return BigDecimal.valueOf(avg)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

    }


}
