package solutions.mystuff.application.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Clears the organization MDC key after every request,
 * removing the need for try/finally blocks in controllers.
 *
 * <p>Controllers still call {@code helper.setOrgMdc(user)}
 * to set the value; this interceptor ensures cleanup.
 */
@Component
public class OrgMdcInterceptor
        implements HandlerInterceptor {

    private static final String MDC_ORG_ID =
            "organizationId";

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler, Exception ex) {
        MDC.remove(MDC_ORG_ID);
    }
}
