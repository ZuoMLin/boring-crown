package edu.tj.cad.boringcrown.biz;

import edu.tj.cad.boringcrown.dao.CriteriaDao;
import edu.tj.cad.boringcrown.domain.entity.CriteriaEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zuomlin
 */
@Component
public class CriteriaBiz {

    @Resource
    private CriteriaDao criteriaDao;

    public int insertCriteria(CriteriaEntity entity) {
        return criteriaDao.insertCriteria(entity);
    }

    public CriteriaEntity findCriteria(int criteriaId) {
        return criteriaDao.findByCriteriaId(criteriaId);
    }

}
