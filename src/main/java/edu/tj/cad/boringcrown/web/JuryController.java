package edu.tj.cad.boringcrown.web;

import com.google.common.collect.Lists;
import edu.tj.cad.boringcrown.biz.ScoreBiz;
import edu.tj.cad.boringcrown.biz.UserBiz;
import edu.tj.cad.boringcrown.common.RestResponse;
import edu.tj.cad.boringcrown.domain.dto.JudgeMessage;
import edu.tj.cad.boringcrown.domain.dto.ScoreDto;
import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.awt.*;
import java.util.List;

/**
 * Created by zuomlin
 */
@Slf4j
@RestController
public class JuryController {

    @Resource
    private ScoreBiz scoreBiz;

    @Resource
    private UserBiz userBiz;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping(value = "/jurys/{juryId}/scores")
    public RestResponse<List<ScoreDto>> getJuryScores(@PathVariable Integer juryId,
                                                      @RequestParam(required = false) Integer productId, @RequestParam(required = false) Integer criteriaId) {
        SecurityUtils.getSubject().checkPermission(String.valueOf(juryId));
        if (juryId == null) {
            return new RestResponse(false, "参数错误", ListUtils.EMPTY_LIST);
        }
        try {
            if (productId != null && criteriaId != null) {
                return new RestResponse(scoreBiz.getScoresByJuryIdAndProductIdAndCriteriaId(juryId, productId, criteriaId));
            }
            if (productId != null) {
                return new RestResponse(scoreBiz.getScoresByJuryIdAndProductId(juryId, productId));
            }
            if (criteriaId != null) {
                return new RestResponse(scoreBiz.getScoresByJuryIdAndCriteriaId(juryId, criteriaId));
            }
            return new RestResponse(scoreBiz.getScoresByJuryId(juryId));
        } catch (Exception e) {
            log.error(String.format("查询分数failed, params: juryId=%s, productId=%s, criteriaId=%s", juryId, productId, criteriaId), e);
            return new RestResponse(false, "查询分数失败", null);
        }
    }

    @PostMapping(value = "/jurys/{juryId}/scores", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse<Boolean> setJuryScores(@PathVariable Integer juryId, @RequestBody List<ScoreDto> scoreDtoList) {
        SecurityUtils.getSubject().checkPermission(String.valueOf(juryId));
        try {
            int affectedRows = scoreBiz.batchReplaceScores(juryId, scoreDtoList);
            if (affectedRows > 0) {
                List<JudgeMessage> judgeMessageList = Lists.newArrayList();
                for (ScoreDto scoreDto : scoreDtoList) {
                    JudgeMessage message = new JudgeMessage();
                    message.setJuryId(juryId);
                    message.setProductId(scoreDto.getProductId());
                    judgeMessageList.add(message);
                }
                messagingTemplate.convertAndSend("/topic/judge", judgeMessageList);
                return new RestResponse(true);
            }
        } catch (Exception e) {
            log.error(String.format("评分failed, params: juryId=%s, scoreDtoList=%s", juryId, ReflectionToStringBuilder.toString(scoreDtoList)), e);
        }
        return new RestResponse(false, "评分失败", false);
    }

    @GetMapping(value = "/jurys/{juryId}/criterias")
    public RestResponse<List<CriteriaEntity>> getJuryCriterias(@PathVariable Integer juryId) {
        SecurityUtils.getSubject().checkPermission(String.valueOf(juryId));
        if (juryId == null) {
            return new RestResponse(false, "参数错误", ListUtils.EMPTY_LIST);
        }
        try {
            return new RestResponse(scoreBiz.getCriteriasByJuryId(juryId));
        } catch (Exception e) {
            log.error(String.format("getJuryCriteria failed, params: juryId=%s", juryId), e);
        }
        return new RestResponse(false, "获取评分标准失败", false);
    }

    @GetMapping(value = "/jurys/{juryId}/products")
    public RestResponse<List<Integer>> getJuryProductIds(@PathVariable Integer juryId, Integer status) {
        SecurityUtils.getSubject().checkPermission(String.valueOf(juryId));
        if (juryId == null || status == null) {
            return new RestResponse(false, "参数错误", ListUtils.EMPTY_LIST);
        }
        if (status == 0) {
            // 未评分作品
            return new RestResponse(scoreBiz.getUnJudgedProductIdsByJuryId(juryId));
        } else if (status == 1) {
            return new RestResponse(scoreBiz.getJudgedProductIdsByJuryId(juryId));
        }
        return new RestResponse(false, "获取作品失败", false);
    }

    @RequestMapping(value = "/jurys/{juryId}/account")
    public RestResponse<Boolean> finishJudge(@PathVariable Integer juryId) {
        SecurityUtils.getSubject().checkPermission(String.valueOf(juryId));
        if (juryId == null) {
            return new RestResponse(false, "参数错误", false);
        }
        List<Integer> productIdList = scoreBiz.getUnJudgedProductIdsByJuryId(juryId);
        if (CollectionUtils.isNotEmpty(productIdList)) {
            return new RestResponse(false, "作品" + productIdList + "还未评分", false);
        }
        int affectedRows = userBiz.finishJudge(juryId);
        if (affectedRows > 0) {
            // TODO: 发送消息
            messagingTemplate.convertAndSend("/topic/finish", juryId);
            return new RestResponse(true);
        }
        return new RestResponse(false, "结束评分失败", false);
    }



}
