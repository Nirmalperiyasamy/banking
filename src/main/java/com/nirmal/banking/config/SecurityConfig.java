package com.nirmal.banking.config;

import com.nirmal.banking.interceptor.AuthJwtFilter;
import com.nirmal.banking.interceptor.JwtUtil;
import com.nirmal.banking.service.UserService;
import com.nirmal.banking.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final AuthJwtFilter authJwtFilter;

    private final String[] PERMITTED_ROUTES = {"/api/user/add-user", "/login"};
    private final String USER_ROUTE = "/api/user/**";
    private final String ADMIN_ROUTE = "/api/admin/**";
    private final String MANAGER_ROUTE = "/api/manager/**";

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().
                disable()
                .authorizeRequests()
                .antMatchers(PERMITTED_ROUTES[0], PERMITTED_ROUTES[1]).permitAll()
                .antMatchers(USER_ROUTE).hasAuthority(Role.USER.name())
                .antMatchers(ADMIN_ROUTE).hasAuthority(Role.ADMIN.name())
                .antMatchers(MANAGER_ROUTE).hasAuthority(Role.MANAGER.name())
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authJwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
