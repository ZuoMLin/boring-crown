package edu.tj.cad.boringcrown.shiro.session;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

/**
 * Created by zuomlin
 */
public class TokenSessionManager extends DefaultWebSessionManager {

    private static final String PRAGMA_TOKEN = "pragma-token";

    @Override
    public Serializable getSessionId(SessionKey key) {
        Serializable sessionId = key.getSessionId();
        if (sessionId == null) {
            HttpServletRequest request = WebUtils.getHttpRequest(key);
            HttpServletResponse response = WebUtils.getHttpResponse(key);
            sessionId = this.getSessionId(request,response);
        }
        HttpServletRequest request = WebUtils.getHttpRequest(key);
        HttpServletResponse response = WebUtils.getHttpResponse(key);
        request.setAttribute(PRAGMA_TOKEN, sessionId.toString());
        Cookie cookie = new Cookie(PRAGMA_TOKEN, sessionId.toString());
        response.addCookie(cookie);

        return sessionId;
//        Serializable id = super.getSessionId(key);
//        HttpServletRequest request = WebUtils.getHttpRequest(key);
//        request.setAttribute(PRAGMA_TOKEN, id.toString());
//        return id;
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String id = httpServletRequest.getHeader(PRAGMA_TOKEN);
        if (StringUtils.isBlank(id)) {
            id = UUID.randomUUID().toString();
        }

        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
                    ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
        //automatically mark it valid here.  If it is invalid, the
        //onUnknownSession method below will be invoked and we'll remove the attribute at that time.
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);

        // always set rewrite flag - SHIRO-361
        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());

        return id;

    }

}
