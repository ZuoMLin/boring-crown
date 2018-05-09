package edu.tj.cad.boringcrown.biz;

import edu.tj.cad.boringcrown.dao.UserDao;
import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zuomlin
 */
@Component
public class UserBiz {

    @Resource
    private UserDao userDao;

    /**
     * 是否所有评委都评分完成
     *
     * @return
     */
    public List<UserEntity> getJurysNotFinished() {
        return userDao.findByStatus(UserEntity.UserStatus.JUDGE_OPEN.status);
    }

    public List<UserEntity> getJurysFinished() {
        return userDao.findByStatus(UserEntity.UserStatus.JUDGE_FINISHED.status);
    }

    public List<UserEntity> getJurys() {
        return userDao.findByRole("jury");
    }

    public List<UserEntity> getUsersByNames(List<String> usernameList) {
        return userDao.findByUsernames(usernameList);
    }

    public int finishJudge(int juryId) {
        return userDao.updateUserStatus(juryId, UserEntity.UserStatus.JUDGE_FINISHED.status);
    }

    public int insertUser(String username, String password, String role, int status) {
        return userDao.insertUser(username, password, role, status);
    }

}
