package edu.tj.cad.boringcrown.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zuomlin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbnormalDto {

    private int productId;

    private int criteriaId;

    private String content1;

    private String content2;

    private String content3;

    private String illustration;

    private double partGrade;

    private int juryIdA;

    private String juryAName;

    private double scoreA;

    private int juryIdB;

    private String juryBName;

    private double scoreB;

    private double scoreAdmin;
}
