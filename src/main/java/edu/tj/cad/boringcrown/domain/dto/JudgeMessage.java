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
public class JudgeMessage {

    private int juryId;

    private int productId;
}
