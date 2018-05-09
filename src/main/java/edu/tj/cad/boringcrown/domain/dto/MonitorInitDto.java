package edu.tj.cad.boringcrown.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by zuomlin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorInitDto implements Serializable {

    private List<Integer> productIds;

    private Map<Integer, String> juryIdJuryNameMap;

    private List<JudgeMessage> judgeMessages;
}
