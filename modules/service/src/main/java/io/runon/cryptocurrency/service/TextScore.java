package io.runon.cryptocurrency.service;

import lombok.Data;

import java.util.Comparator;

/**
 * 문자열과 점수
 * @author macle
 */
@Data
public class TextScore {

    public final static Comparator<TextScore> SORT_DESC = (t1, t2) -> Double.compare(t2.score, t1.score);


    private String text;
    private double score = 0;


    public TextScore(){

    }

    public TextScore(String text, double score){
        this.text = text;
        this.score = score;

    }
}
