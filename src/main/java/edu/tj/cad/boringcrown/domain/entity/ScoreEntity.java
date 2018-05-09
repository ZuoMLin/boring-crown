package edu.tj.cad.boringcrown.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by zuomlin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreEntity {

    private int juryId;

    private int productId;

    private int criteriaId;

    private double score;
}
