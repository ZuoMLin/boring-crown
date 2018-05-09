package edu.tj.cad.boringcrown.dao;

import edu.tj.cad.boringcrown.bo.CriteriaScoreBo;
import edu.tj.cad.boringcrown.bo.ScoreAbnormalBo;
import edu.tj.cad.boringcrown.domain.dto.JudgeMessage;
import edu.tj.cad.boringcrown.domain.dto.ScoreDto;
import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import edu.tj.cad.boringcrown.domain.entity.ScoreEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zuomlin
 */
@Mapper
@Component
public interface ScoreDao {

    @Select("SELECT * FROM SCORE WHERE juryid = #{juryId} AND productid = #{productId} AND criteriaid = #{criteriaId} ORDER BY criteriaid")
    List<ScoreEntity> findByJuryIdAndProductIdAndCriteriaId(@Param("juryId") int juryId, @Param("productId") int productId, @Param("criteriaId") int criteriaId);

    @Select("SELECT * FROM SCORE WHERE juryid = #{juryId} AND productid = #{productId} ORDER BY criteriaid")
    List<ScoreEntity> findByJuryIdAndProductId(@Param("juryId") int juryId, @Param("productId") int productId);

    @Select("SELECT * FROM SCORE WHERE juryid = #{juryId} AND criteriaid = #{criteriaId} ORDER BY criteriaid")
    List<ScoreEntity> findByJuryIdAndCriteriaId(@Param("juryId") int juryId, @Param("criteriaId") int criteriaId);

    @Select("SELECT * FROM SCORE WHERE juryid = #{juryId} ORDER BY criteriaid")
    List<ScoreEntity> findByJuryId(@Param("juryId") int juryId);

    @Insert("REPLACE INTO SCORE(juryid, productid, criteriaid, score) VALUES(#{juryId}, #{dto.productId}, #{dto.criteriaId}, #{dto.score})")
    int replaceScore(@Param("juryId") int juryId, @Param("dto") ScoreDto scoreDto);

    @Insert({
            "<script>",
            "REPLACE INTO SCORE(juryid, productid, criteriaid, score) VALUES",
            "<foreach collection='dtos' item='dto' separator=','>",
            "(#{juryId}, #{dto.productId}, #{dto.criteriaId}, #{dto.score})",
            "</foreach>",
            "</script>"
    })
    int batchReplaceScores(@Param("juryId") int juryId, @Param("dtos") List<ScoreDto> scoreDtoList);

    @Select("SELECT s.juryid, s.productid, s.criteriaid, score, partgrade FROM SCORE s, CRITERIA c WHERE (s.productid, s.criteriaid) IN (SELECT productid, SCORE.criteriaid FROM SCORE, CRITERIA where SCORE.criteriaid = CRITERIA.criteriaid GROUP BY productid, SCORE.criteriaid, partgrade HAVING COUNT(*) = 2 AND MAX(score) - MIN(score) > partgrade * #{abnormalPct}) AND s.criteriaid = c.criteriaid")
    List<ScoreAbnormalBo> findAllAbnormalItems(@Param("abnormalPct") double abnormalPct);

    @Select("SELECT * FROM CRITERIA WHERE criteriaid in (SELECT DISTINCT criteriaid FROM SCORE WHERE juryid = #{juryId}) ORDER BY criteriaid")
    List<CriteriaEntity> findCriteriasByJuryId(@Param("juryId") int juryId);

    @Select("SELECT score.criteriaid, content1, content2, content3, partgrade, (SUM(score) - MAX(score) - MIN(score)) / (#{count} - 2) as score FROM score, criteria WhERE productid = #{productId} AND type = '主观性' AND score.criteriaid = criteria.criteriaid GROUP BY score.criteriaid")
    List<CriteriaScoreBo> findSubjectKickAvgScores(@Param("productId") int productId, @Param("count") int count);

    @Select("SELECT score.criteriaid, content1, content2, content3, partgrade, AVG(score) as score FROM score, criteria WhERE productid = #{productId} AND type = '主观性' AND score.criteriaid = criteria.criteriaid GROUP BY score.criteriaid")
    List<CriteriaScoreBo> findSubjectAvgScores(@Param("productId") int productId);

    @Select("SELECT score.criteriaid, content1, content2, content3, partgrade, AVG(score) as score FROM score, criteria WhERE productid = #{productId} AND type = '客观性' AND score.criteriaid = criteria.criteriaid GROUP BY score.criteriaid")
    List<CriteriaScoreBo> findObjectAvgScores(@Param("productId") int productId);

    @Select("SELECT DISTINCT productid FROM score WHERE juryid = #{juryId} AND score is NULL")
    List<Integer> findUnJudgedProductIdsByJuryId(@Param("juryId") int juryId);

    @Select("SELECT DISTINCT productid FROM score WHERE juryid = #{juryId} AND score is not NULL")
    List<Integer> findJudgedProductIdsByJuryId(@Param("juryId") int juryId);

    @Select("SELECT DISTINCT juryid, productid FROM score WHERE score IS NOT NULL")
    List<JudgeMessage> findJudgeMessages();

}
