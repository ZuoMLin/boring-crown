package edu.tj.cad.boringcrown.shiro.realm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.tj.cad.boringcrown.dao.UserDao;
import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zuomlin
 */
public class UsernamePasswordRealm extends AuthorizingRealm {

    @Resource
    private UserDao userDao;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.fromRealm(getName()).iterator().next();
        if (StringUtils.isNotBlank(username)) {
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
            List<UserEntity> userEntityList = userDao.findByUsernames(Lists.newArrayList(username));
            if (CollectionUtils.isNotEmpty(userEntityList) && userEntityList.get(0) != null) {
                authorizationInfo.setRoles(Sets.newHashSet(userEntityList.get(0).getRole().split(",|，")));
                authorizationInfo.setStringPermissions(Sets.newHashSet(String.valueOf(userEntityList.get(0).getUserId())));
                return authorizationInfo;
            }
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        if (StringUtils.isNotBlank(username)) {
            List<UserEntity> userEntityList = userDao.findByUsernames(Lists.newArrayList(username));
            if (CollectionUtils.isNotEmpty(userEntityList) && userEntityList.get(0) != null) {
                if (userEntityList.get(0).getStatus() != UserEntity.UserStatus.JUDGE_FINISHED.status) {
                    return new SimpleAuthenticationInfo(userEntityList.get(0).getUsername(), userEntityList.get(0).getPassword(), getName());
                }
            }
        }
        return null;
    }
}
