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
public class ScoreAbnormalBo {

    private int juryId;

    private int productId;

    private int criteriaId;

    private double partGrade;

    private double score;
}
