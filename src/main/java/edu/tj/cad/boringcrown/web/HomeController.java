package edu.tj.cad.boringcrown.web;

import com.google.common.collect.Lists;
import edu.tj.cad.boringcrown.biz.UserBiz;
import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by zuomlin
 */
@Slf4j
@Controller
public class HomeController {

    @Resource
    private UserBiz userBiz;

    @RequestMapping({"/", "/index"})
    public String index(Model model) {
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();
        List<UserEntity> userEntityList = userBiz.getUsersByNames(Lists.newArrayList(username));
        if (CollectionUtils.isNotEmpty(userEntityList) && userEntityList.get(0) != null) {
            model.addAttribute("username", userEntityList.get(0).getUsername());
            model.addAttribute("userId", userEntityList.get(0).getUserId());
            return "index";
        } else {
            return "login";
        }
    }

    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Map<String, Object> map) throws Exception{
        System.out.println("HomeController.login()");
        // 登录失败从request中获取shiro处理的异常信息。
        // shiroLoginFailure:就是shiro异常类的全类名.
        String exception = (String) request.getAttribute("shiroLoginFailure");
        System.out.println("exception=" + exception);
        String msg = "";
        if (exception != null) {
            if (UnknownAccountException.class.getName().equals(exception)) {
                log.error(String.format("账号不存在, params: request=%s", ReflectionToStringBuilder.toString(request)), exception);
                System.out.println("账号不存在");
                msg = "账号不存在";
            } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
                log.error(String.format("密码不正确, params: request=%s", ReflectionToStringBuilder.toString(request)), exception);
                msg = "密码不正确";
            } else {
                log.error(String.format("登录failed, params: request=%s", ReflectionToStringBuilder.toString(request)), exception);
                msg = "登录失败";
            }
        }
        map.put("msg", msg);
        // 此方法不处理登录成功,由shiro进行处理
        return "login";
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        System.out.println("---------没有权限---------");
        return "403";
    }
}
