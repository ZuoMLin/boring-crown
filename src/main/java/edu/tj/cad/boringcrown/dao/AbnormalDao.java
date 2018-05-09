package edu.tj.cad.boringcrown.dao;

import edu.tj.cad.boringcrown.bo.CriteriaScoreBo;
import edu.tj.cad.boringcrown.bo.ScoreAbnormalBo;
import edu.tj.cad.boringcrown.domain.dto.AbnormalDto;
import edu.tj.cad.boringcrown.domain.entity.AbnormalEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zuomlin
 */
@Mapper
@Component
public interface AbnormalDao {

    @Select("SELECT COUNT(*) FROM ABNORMAL")
    int countAbnormalRows();

    @Select("SELECT * FROM ABNORMAL WHERE scoreadmin is NULL AND ABS(scorea-scoreb) > partgrade * #{abnormalPct}")
    List<AbnormalEntity> findAllAbnormals(@Param("abnormalPct") double abnormalPct);

    @Select("SELECT * FROM ABNORMAL WHERE productid = #{productId} AND criteriaid = #{criteriaId}")
    AbnormalEntity findByProductIdAndCriteriaId(@Param("productId") int productId, @Param("criteriaId") int criteriaId);

    @Insert("INSERT INTO ABNORMAL(productid, criteriaid, partgrade, juryida, scorea, juryidb, scoreb) VALUES(#{productId}, #{criteriaId}, #{partGrade}, #{juryA.juryId}, #{juryA.score}, #{juryB.juryId}, #{juryB.score})")
    int insertAbnormal(@Param("productId") int productId, @Param("criteriaId") int criteriaId, @Param("partGrade") double partGrade,
                        @Param("juryA") ScoreAbnormalBo juryA, @Param("juryB") ScoreAbnormalBo juryB);

    @Update("UPDATE ABNORMAL SET scorea = #{entity.scoreA}, scoreb = #{entity.scoreB} WHERE productid = #{entity.productId} AND criteriaid = #{entity.criteriaId}")
    int updateAbnormal(@Param("entity") AbnormalDto entity);

    @Select("SELECT abnormal.criteriaid, content1, content2, content3, abnormal.partgrade, (scorea + scoreb) / 2 as score FROM abnormal, criteria WHERE productid = #{productId} AND abnormal.criteriaid = criteria.criteriaid GROUP BY criteriaid ORDER BY criteriaid")
    List<CriteriaScoreBo> findAvgScoresByProductId(@Param("productId") int productId);

    @Select("SELECT abnormal.criteriaid, content1, content2, content3, abnormal.partgrade, scoreadmin as score FROM abnormal, criteria WHERE productid = #{productId} AND abnormal.criteriaid = criteria.criteriaid GROUP BY criteriaid ORDER BY criteriaid")
    List<CriteriaScoreBo> findAdminScoresByProductId(@Param("productId") int productId);
}
