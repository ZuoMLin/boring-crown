package edu.tj.cad.boringcrown.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zuomlin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaEntity {

    private int criteriaId;

    private String content1;

    private String content2;

    private String content3;

    private String Type;

    private double partGrade;

    private double totalGrade;

    private String illustration;
}
