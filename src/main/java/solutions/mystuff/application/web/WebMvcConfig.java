package solutions.mystuff.application.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation
        .InterceptorRegistry;
import org.springframework.web.servlet.config.annotation
        .WebMvcConfigurer;

/**
 * Registers web interceptors.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final OrgMdcInterceptor orgMdcInterceptor;

    public WebMvcConfig(
            OrgMdcInterceptor orgMdcInterceptor) {
        this.orgMdcInterceptor = orgMdcInterceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry) {
        registry.addInterceptor(orgMdcInterceptor);
    }
}
