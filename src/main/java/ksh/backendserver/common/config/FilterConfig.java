package ksh.backendserver.common.config;

import ksh.backendserver.common.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final LoginFilter loginFilter;

    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilterRegistration() {
        FilterRegistrationBean<LoginFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(loginFilter);
        registration.addUrlPatterns("/subscriptions");
        registration.setOrder(1);
        return registration;
    }
}