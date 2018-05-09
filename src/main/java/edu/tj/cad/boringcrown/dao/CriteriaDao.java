package edu.tj.cad.boringcrown.dao;

import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * Created by zuomlin
 */
@Mapper
@Component
public interface CriteriaDao {

//    @Insert({"INSERT INTO CRITERIA(content1, content2, content3, type, partgrade, totalgrade, illustration) VALUES(#{entity.content1}, #{entity.content2}, #{entity.content3}, #{entity.type}, #{entity.partGrade}, #{entity.totalGrade}, #{entity.illustration})"})
//    @Options(useGeneratedKeys = true, keyProperty = "criteriaId", keyColumn = "criteriaid")
//    int insertCriteria(@Param("entity") CriteriaEntity entity);

    @Insert({"INSERT INTO CRITERIA(content1, content2, content3, type, partgrade, totalgrade, illustration) VALUES(#{content1}, #{content2}, #{content3}, #{type}, #{partGrade}, #{totalGrade}, #{illustration})"})
    @Options(useGeneratedKeys = true, keyProperty = "criteriaId", keyColumn = "criteriaid")
    int insertCriteria(CriteriaEntity entity);

    @Select("SELECT * FROM CRITERIA WHERE criteriaid = #{criteriaId}")
    CriteriaEntity findByCriteriaId(@Param("criteriaId") int criteriaId);
}
