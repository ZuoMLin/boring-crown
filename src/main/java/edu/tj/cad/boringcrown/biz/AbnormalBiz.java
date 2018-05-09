package edu.tj.cad.boringcrown.biz;

import com.google.common.collect.Lists;
import edu.tj.cad.boringcrown.bo.CriteriaScoreBo;
import edu.tj.cad.boringcrown.bo.ScoreAbnormalBo;
import edu.tj.cad.boringcrown.common.constants.Config;
import edu.tj.cad.boringcrown.dao.AbnormalDao;
import edu.tj.cad.boringcrown.dao.CriteriaDao;
import edu.tj.cad.boringcrown.dao.UserDao;
import edu.tj.cad.boringcrown.domain.dto.AbnormalDto;
import edu.tj.cad.boringcrown.domain.entity.AbnormalEntity;
import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import org.apache.commons.collections.ListUtils;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zuomlin
 */
@Component
public class AbnormalBiz {

    @Resource
    private AbnormalDao abnormalDao;

    @Resource
    private UserDao userDao;

    @Resource
    private CriteriaDao criteriaDao;

    public int getAbnormalRowsCount() {
        return abnormalDao.countAbnormalRows();
    }

    public AbnormalEntity getAbnormalByProductIdAndCriteriaId(int productId, int criteriaId) {
        return abnormalDao.findByProductIdAndCriteriaId(productId, criteriaId);
    }

    /**
     * 获取abnormal表中异常的记录
     *
     * @param abnormalPct
     * @return
     */
    public List<AbnormalDto> getAllAbnormals(double abnormalPct) {
        List<AbnormalDto> result = Lists.newArrayList();
        List<AbnormalEntity> abnormalEntityList = abnormalDao.findAllAbnormals(abnormalPct);
        if (CollectionUtils.isEmpty(abnormalEntityList)) {
            return result;
        }
        for (AbnormalEntity entity : abnormalEntityList) {
            if (entity != null && entity.getJuryIdA() > 0 && entity.getJuryIdB() > 0) {
                AbnormalDto abnormalDto = new AbnormalDto();
                abnormalDto.setProductId(entity.getProductId());
                abnormalDto.setCriteriaId(entity.getCriteriaId());
                abnormalDto.setPartGrade(entity.getPartGrade());
                abnormalDto.setJuryIdA(entity.getJuryIdA());
                UserEntity juryA = userDao.findByUserId(entity.getJuryIdA());
                abnormalDto.setJuryAName(juryA.getUsername());
                abnormalDto.setScoreA(entity.getScoreA());
                abnormalDto.setJuryIdB(entity.getJuryIdB());
                UserEntity juryB = userDao.findByUserId(entity.getJuryIdB());
                abnormalDto.setJuryBName(juryB.getUsername());
                abnormalDto.setScoreB(entity.getScoreB());
                abnormalDto.setScoreAdmin(entity.getScoreAdmin());
                CriteriaEntity criteriaEntity = criteriaDao.findByCriteriaId(entity.getCriteriaId());
                abnormalDto.setContent1(criteriaEntity.getContent1());
                abnormalDto.setContent2(criteriaEntity.getContent2());
                abnormalDto.setContent3(criteriaEntity.getContent3());
                abnormalDto.setIllustration(criteriaEntity.getIllustration());
                result.add(abnormalDto);
            }
        }
        return result;
    }

    public int insertAbnormal(List<ScoreAbnormalBo> scoreAbnormalBoList) {
        if (CollectionUtils.isEmpty(scoreAbnormalBoList) || scoreAbnormalBoList.size() != 2) {
            return 0;
        }
        ScoreAbnormalBo juryAScore = scoreAbnormalBoList.get(0);
        ScoreAbnormalBo juryBScore = scoreAbnormalBoList.get(1);
        if (juryAScore == null || juryBScore == null) {
            return 0;
        }
        return abnormalDao.insertAbnormal(juryAScore.getProductId(), juryAScore.getCriteriaId(), juryAScore.getPartGrade(), juryAScore, juryBScore);
    }

    public int updateAbnormal(AbnormalDto abnormalDto) {
        return abnormalDao.updateAbnormal(abnormalDto);
    }

    /**
     * 获取作品异常评分最终结果
     *
     * @param productId
     * @return
     */
    public List<CriteriaScoreBo> getCriteriaScores(int productId, int pattern) {
        if (Config.SubjectJudgePattern.KICK_AVG_PATTERN.pattern == pattern) {
            return abnormalDao.findAvgScoresByProductId(productId);
        } else if (Config.SubjectJudgePattern.AVG_PATTERN.pattern == pattern) {
            return abnormalDao.findAdminScoresByProductId(productId);
        }
        return ListUtils.EMPTY_LIST;
    }

}
