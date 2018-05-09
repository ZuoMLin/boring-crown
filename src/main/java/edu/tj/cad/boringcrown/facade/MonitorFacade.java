package edu.tj.cad.boringcrown.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.tj.cad.boringcrown.biz.ScoreBiz;
import edu.tj.cad.boringcrown.biz.UserBiz;
import edu.tj.cad.boringcrown.common.constants.Config;
import edu.tj.cad.boringcrown.domain.dto.JudgeMessage;
import edu.tj.cad.boringcrown.domain.dto.MonitorInitDto;
import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by zuomlin
 */
@Component
public class MonitorFacade {

    @Resource
    private UserBiz userBiz;

    @Resource
    private ScoreBiz scoreBiz;

    public MonitorInitDto getMonitorInitDto() throws Exception {
        Map<Integer, String> juryIdJuryNameMap = Maps.newLinkedHashMap();
        List<UserEntity> userEntityList = userBiz.getJurys();
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            for (UserEntity userEntity : userEntityList) {
                juryIdJuryNameMap.put(userEntity.getUserId(), userEntity.getUsername());
            }
        }
        List<Integer> productIdList = Lists.newArrayList();
        for (int productId = Config.HEAD_PRODUCTID; productId <= Config.TAIL_PRODUCTID; ++productId) {
            productIdList.add(productId);
        }
        List<JudgeMessage> judgeMessageList = scoreBiz.getJudgeMessages();
        MonitorInitDto result = new MonitorInitDto();
        result.setProductIds(productIdList);
        result.setJudgeMessages(judgeMessageList);
        result.setJuryIdJuryNameMap(juryIdJuryNameMap);
        return result;
    }
}
