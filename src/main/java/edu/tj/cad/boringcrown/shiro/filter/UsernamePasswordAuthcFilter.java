package edu.tj.cad.boringcrown.shiro.filter;

import com.google.common.collect.Sets;
import org.apache.commons.collections.SetUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Set;

/**
 * Created by zuomlin
 */
public class UsernamePasswordAuthcFilter extends FormAuthenticationFilter {

    public static final String DEFAULT_LOGIN_URL = "/login";

    public UsernamePasswordAuthcFilter() {
        setLoginUrl(DEFAULT_LOGIN_URL);
    }

    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        boolean isAuthenticated = subject.isAuthenticated();
        boolean isLoginRequest = isLoginRequest(request, response);
        if (isAuthenticated) {
            if (isLoginRequest) {
                return false;
            }
            return true;
        }
        return false;
//        if (super.isAccessAllowed(request, response, mappedValue)) {
//            if (isLoginRequest(request, response)) {
//                Subject subject = SecurityUtils.getSubject();
//
//            }
//        }
//        return false;
//
//
//        HttpServletRequest httpRequest = WebUtils.toHttp(request);
//        String httpMethod = httpRequest.getMethod();
//
//        // Check whether the current request's method requires authentication.
//        // If no methods have been configured, then all of them require auth,
//        // otherwise only the declared ones need authentication.
//        String[] options = (String[]) mappedValue;
//        Set<String> methods = SetUtils.EMPTY_SET;
//        if (options != null) {
//            methods = Sets.newHashSet((String[]) mappedValue);
//        }
//        boolean authcRequired = methods.size() == 0;
//        for (String m : methods) {
//            if (httpMethod.toUpperCase(Locale.ENGLISH).equals(m)) { // list of methods is in upper case
//                authcRequired = true;
//                break;
//            }
//        }
//
//        if (authcRequired) {
//            return super.isAccessAllowed(request, response, mappedValue);
//        }
//        else {
//            return true;
//        }
    }

    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {
        WebUtils.getAndClearSavedRequest(request);
        if (subject.hasRole("admin")) {
            WebUtils.redirectToSavedRequest(request, response, "/admin");
        } else {
            issueSuccessRedirect(request, response);
        }
        //we handled the success redirect directly, prevent the chain from continuing:
        return false;
    }

}
