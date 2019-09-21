package me.lnsprt.projects.restblog.security.config;

import me.lnsprt.projects.restblog.security.filter.AuthenticationFilter;
import me.lnsprt.projects.restblog.security.filter.AuthorizationFilter;
import me.lnsprt.projects.restblog.security.service.UserAuthDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserAuthDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RoleHierarchy roleHierarchy;
    private final CorsConfigurationSource corsConfig;

    @Autowired
    public SecurityConfig(UserAuthDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                          RoleHierarchy roleHierarchy, @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.roleHierarchy = roleHierarchy;
        this.corsConfig = corsConfig;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .addFilter(new AuthorizationFilter(authenticationManager()))
                .addFilter(new AuthenticationFilter(userDetailsService, authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(corsConfig);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }


    @Override
    public void configure(WebSecurity web) {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        web.expressionHandler(expressionHandler);
    }
}
