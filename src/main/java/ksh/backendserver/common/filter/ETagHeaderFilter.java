package ksh.backendserver.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import java.io.IOException;

@Configuration
public class ETagHeaderFilter {

    private static final String SCREENSHOT_PATH_PATTERN = "/api/v1/posts/\\d+/screenshot";
    private static final String COMPANY_LOGO_PATH_PATTERN = "/api/v1/companies/\\d+/logo";
    private static final String POSTS_URL_PATTERN = "/api/v1/posts/*";
    private static final String COMPANIES_URL_PATTERN = "/api/v1/companies/*";

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> etagFilter() {
        OncePerRequestFilter filter = new OncePerRequestFilter() {
            private final ShallowEtagHeaderFilter etagFilter = new ShallowEtagHeaderFilter();

            @Override
            protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
            ) throws ServletException, IOException {
                String path = request.getRequestURI();
                if (path.matches(SCREENSHOT_PATH_PATTERN) || path.matches(COMPANY_LOGO_PATH_PATTERN)) {
                    etagFilter.doFilter(request, response, filterChain);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        };

        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns(POSTS_URL_PATTERN, COMPANIES_URL_PATTERN);
        return registration;
    }
}
