package com.nirmal.banking.config;

import com.nirmal.banking.service.UserService;
import com.nirmal.banking.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final String[] PERMITTED_ROUTES = {"/api/user/add-user", "/api/user/**", "/api/admin/**", "/api/manager/**"};

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authProvider;
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(PERMITTED_ROUTES[0]).permitAll()
                .antMatchers(PERMITTED_ROUTES[1]).hasAuthority(Role.USER.name())
                .antMatchers(PERMITTED_ROUTES[2]).hasAuthority(Role.ADMIN.name())
                .antMatchers(PERMITTED_ROUTES[3]).hasAuthority(Role.MANAGER.name())
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }
}
