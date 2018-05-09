package edu.tj.cad.boringcrown.biz;

import com.google.common.collect.Lists;
import edu.tj.cad.boringcrown.bo.CriteriaScoreBo;
import edu.tj.cad.boringcrown.bo.ScoreAbnormalBo;
import edu.tj.cad.boringcrown.common.constants.Config;
import edu.tj.cad.boringcrown.dao.ScoreDao;
import edu.tj.cad.boringcrown.domain.dto.JudgeMessage;
import edu.tj.cad.boringcrown.domain.dto.ScoreDto;
import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import edu.tj.cad.boringcrown.domain.entity.ScoreEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zuomlin
 */
@Component
public class ScoreBiz {

    @Resource
    private ScoreDao scoreDao;

    public List<CriteriaEntity> getCriteriasByJuryId(int juryId) {
        return scoreDao.findCriteriasByJuryId(juryId);
    }

    public List<ScoreDto> getScoresByJuryIdAndProductIdAndCriteriaId(int juryId, int productId, int criteriaId) {
        return transScoreEntityListToScoreDtoList(scoreDao.findByJuryIdAndProductIdAndCriteriaId(juryId, productId, criteriaId));
    }

    public List<ScoreDto> getScoresByJuryIdAndProductId(int juryId, int productId) {
        return transScoreEntityListToScoreDtoList(scoreDao.findByJuryIdAndProductId(juryId, productId));
    }

    public List<ScoreDto> getScoresByJuryIdAndCriteriaId(int juryId, int criteriaId) {
        return transScoreEntityListToScoreDtoList(scoreDao.findByJuryIdAndCriteriaId(juryId, criteriaId));
    }

    public List<ScoreDto> getScoresByJuryId(int juryId) {
        return transScoreEntityListToScoreDtoList(scoreDao.findByJuryId(juryId));
    }

    public List<Integer> getUnJudgedProductIdsByJuryId(int juryId) {
        return scoreDao.findUnJudgedProductIdsByJuryId(juryId);
    }

    public List<Integer> getJudgedProductIdsByJuryId(int juryId) {
        return scoreDao.findJudgedProductIdsByJuryId(juryId);
    }

    public int replaceScore(int juryId, ScoreDto scoreDto) {
        return scoreDao.replaceScore(juryId, scoreDto);
    }

    public int batchReplaceScores(int juryId, List<ScoreDto> scoreDtoList) {
        return scoreDao.batchReplaceScores(juryId, scoreDtoList);
    }

    /**
     * 获取所有score表中客观题评分异常项
     *
     * @param abnormalPct
     * @return
     */
    public List<ScoreAbnormalBo> getAllAbnormalItems(double abnormalPct) {
        return scoreDao.findAllAbnormalItems(abnormalPct);
    }

    /**
     * 获取作品主观题评分结果
     *
     * @return
     */
    public List<CriteriaScoreBo> getSujectCriteriaScores(int productId, int juryCount, int pattern) {
        if (Config.SubjectJudgePattern.KICK_AVG_PATTERN.pattern == pattern) {
            return scoreDao.findSubjectKickAvgScores(productId, juryCount);
        } else if (Config.SubjectJudgePattern.AVG_PATTERN.pattern == pattern) {
            return scoreDao.findSubjectAvgScores(productId);
        }
        return ListUtils.EMPTY_LIST;
    }

    /**
     * 获取作品客观题评分结果
     *
     * @param productId
     * @return
     */
    public List<CriteriaScoreBo> getObjectCriteriaScores(int productId) {
        return scoreDao.findObjectAvgScores(productId);
    }

    private List<ScoreDto> transScoreEntityListToScoreDtoList(List<ScoreEntity> scoreEntityList) {
        List<ScoreDto> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(scoreEntityList)) {
            for (ScoreEntity scoreEntity : scoreEntityList) {
                if (scoreEntity != null) {
                    ScoreDto scoreDto = new ScoreDto(scoreEntity.getProductId(), scoreEntity.getCriteriaId(), scoreEntity.getScore());
                    result.add(scoreDto);
                }
            }
        }
        return result;
    }

    public List<JudgeMessage> getJudgeMessages() {
        return scoreDao.findJudgeMessages();
    }


}
