package edu.tj.cad.boringcrown.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by zuomlin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto implements Serializable {

    private int productId;

    private int criteriaId;

    private Double score;
}
