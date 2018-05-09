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
public class AbnormalEntity {

    private int productId;

    private int criteriaId;

    private double partGrade;

    private int juryIdA;

    private double scoreA;

    private int juryIdB;

    private double scoreB;

    private double scoreAdmin;

}
