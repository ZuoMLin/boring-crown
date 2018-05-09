package edu.tj.cad.boringcrown.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.tj.cad.boringcrown.biz.*;
import edu.tj.cad.boringcrown.bo.ProductIdCriteriaIdPair;
import edu.tj.cad.boringcrown.bo.ScoreAbnormalBo;
import edu.tj.cad.boringcrown.common.constants.Config;
import edu.tj.cad.boringcrown.domain.dto.AbnormalDto;
import edu.tj.cad.boringcrown.domain.dto.ScoreDto;
import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by zuomlin
 */
@Component
public class AdminFacade {

    @Resource
    private UserBiz userBiz;

    @Resource
    private AbnormalBiz abnormalBiz;

    @Resource
    private ScoreBiz scoreBiz;

    @Resource
    private CriteriaBiz criteriaBiz;

    @Resource
    private ReportBiz reportBiz;

    /**
     * 获取异常评分项
     * 必须所有评委评分完成
     *
     * @return
     * @throws Exception
     */
    @Transactional
    public List<AbnormalDto> getAbnormalDtoItems() throws Exception {
        // 检查所有的评委评分状态是否完成，完成再开启检查
        List<UserEntity> userEntityList = userBiz.getJurysNotFinished();
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            throw new Exception(String.format("评委%s未完成评分", userEntityList));
        }
        int abnormalRowsCount = abnormalBiz.getAbnormalRowsCount();
        if (abnormalRowsCount == 0) {
            // abnormal表中无记录
            // 检查score表中记录是否有异常,有异常插入abnormal表中
            List<ScoreAbnormalBo> scoreAbnormalBoList = scoreBiz.getAllAbnormalItems(Config.ABNORMAL_PCT);
            if (CollectionUtils.isEmpty(scoreAbnormalBoList)) {
                return ListUtils.EMPTY_LIST;
            }
            Map<ProductIdCriteriaIdPair, List<ScoreAbnormalBo>> scoreAbnormalBoListMap = Maps.newHashMap();
            for (ScoreAbnormalBo scoreAbnormalBo : scoreAbnormalBoList) {
                if (scoreAbnormalBo != null) {
                    ProductIdCriteriaIdPair pair = new ProductIdCriteriaIdPair(scoreAbnormalBo.getProductId(), scoreAbnormalBo.getCriteriaId());
                    if (scoreAbnormalBoListMap.get(pair) == null) {
                        scoreAbnormalBoListMap.put(pair, Lists.newArrayList(scoreAbnormalBo));
                    } else {
                        scoreAbnormalBoListMap.get(pair).add(scoreAbnormalBo);
                    }
                }
            }
            for (List<ScoreAbnormalBo> scoreAbnormalBoListTemp : scoreAbnormalBoListMap.values()) {
                abnormalBiz.insertAbnormal(scoreAbnormalBoListTemp);
            }
        }
        // abnormal表中有记录
        List<AbnormalDto> abnormalDtoListFromAbnormal = abnormalBiz.getAllAbnormals(Config.ABNORMAL_PCT);
        if (CollectionUtils.isNotEmpty(abnormalDtoListFromAbnormal)) {
            return abnormalDtoListFromAbnormal;
        }
        return ListUtils.EMPTY_LIST;
    }

    /**
     * 异常项再评分
     *
     * @param abnormalDto
     * @return
     */
    public int setAbnormalScore(AbnormalDto abnormalDto) {
        return abnormalBiz.updateAbnormal(abnormalDto);
    }

    @Transactional
    public boolean importCriterias(MultipartFile file) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] criteriaItem = line.split("\\s+");
            CriteriaEntity criteriaEntity = new CriteriaEntity();
            criteriaEntity.setContent1(criteriaItem[0]);
            criteriaEntity.setTotalGrade(Double.valueOf(criteriaItem[1]));
            criteriaEntity.setContent2(criteriaItem[2]);
            criteriaEntity.setContent3(criteriaItem[3]);
            criteriaEntity.setType(criteriaItem[4]);
            criteriaEntity.setPartGrade(Double.valueOf(criteriaItem[5]));
            criteriaEntity.setIllustration(criteriaItem[6]);

            String[] juryNames = criteriaItem[7].split(",|，");

            // 检查导入的评分标准表格式是否正确
            int juryCount = juryNames.length;
            String type = criteriaItem[4];
            if (!((type.equals("客观性") && juryCount == 2) || (type.equals("主观性") && juryCount == Config.SUBJECT_JUDGE_COUNT))) {
                System.out.println(juryNames);
                throw new Exception("criterias import failed caused by jury count not fit the criteria type, line: " + line);
            }
            int affectedRows = criteriaBiz.insertCriteria(criteriaEntity);
            if (affectedRows <= 0) {
                throw new Exception("criteriaEntity insert failed, params: entity=" + ReflectionToStringBuilder.toString(criteriaEntity));
            }
            int criteriaId = criteriaEntity.getCriteriaId();

            List<UserEntity> userList = userBiz.getUsersByNames(Lists.newArrayList(juryNames));
            for (UserEntity user : userList) {
                int juryId = user.getUserId();
                List<ScoreDto> scoreDtoList = Lists.newArrayList();
                for (int productId = Config.HEAD_PRODUCTID; productId <= Config.TAIL_PRODUCTID; ++productId) {
                    ScoreDto scoreDto = new ScoreDto();
                    scoreDto.setProductId(productId);
                    scoreDto.setCriteriaId(criteriaId);
                    scoreDtoList.add(scoreDto);
                }
                scoreBiz.batchReplaceScores(juryId, scoreDtoList);
            }
        }
        return true;
    }

    @Transactional
    public boolean importJurys(MultipartFile file) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] userItem = line.split("\\s+");
            userBiz.insertUser(userItem[0], userItem[1], userItem[2], Integer.valueOf(userItem[3]));
        }
        return true;
    }

    public boolean genJuryReports() throws Exception {
        return reportBiz.genJuryReports();
    }

    public boolean genProductReports() throws Exception {
        return reportBiz.genProductReports();
    }


}
