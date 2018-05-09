package edu.tj.cad.boringcrown.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zuomlin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaScoreBo {

    private int criteriaId;

    private String content1;

    private String content2;

    private String content3;

    private double partgrade;

    private double score;

}
