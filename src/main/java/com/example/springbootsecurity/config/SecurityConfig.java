package com.example.springbootsecurity.config;

import com.example.springbootsecurity.account.AccountService;
import com.example.springbootsecurity.common.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;


    public AccessDecisionManager accessDecisionManager(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
        webExpressionVoter.setExpressionHandler(handler);

        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);
        return new AffirmativeBased(voters);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.ignoring().mvcMatchers("/favicon.ico");
        //????????? ?????? ??????
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    public SecurityExpressionHandler expressionHandler(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        return handler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new LoggingFilter() , WebAsyncManagerIntegrationFilter.class);

        http.authorizeRequests()
                    .mvcMatchers("/","/info","/account/**","/signup" , "/h2-console/**").permitAll()
                    .mvcMatchers("/admin").hasRole("ADMIN")
                    .mvcMatchers("/user").hasRole("USER")
                    .anyRequest().authenticated()
                    .expressionHandler(expressionHandler());
                //.accessDecisionManager(accessDecisionManager())
                 //   .and()
                //.formLogin()
                //    .and()
                //.httpBasic();
        http.formLogin();
        http.httpBasic();
        http.formLogin()
                .loginPage("/login")
                .permitAll();

        http.rememberMe()
                .userDetailsService(accountService)
                .key("remeber-me-sample")
        ;

        http.logout().logoutSuccessUrl("/");

        /*http.sessionManagement()
                .sessionFixation()
                    .changeSessionId()
                .maximumSessions(1)
                    .maxSessionsPreventsLogin(false);//???????????? false , true??? ?????? ??? ????????????????????? ??????????????? ?????? ???????????? ????????? ??????
        ;*/

        // TODO ExceptionTranslatorFilter -> FilterSecurityInterceptor (AccessDecisionManager , AffirmativeBased)
        // TODO AuthenticationException -> AuthenticationEntryPoint
        // TODO AccessDeniedException -> AccessDeniedHandler

        http.exceptionHandling()
                //.accessDeniedPage("/access-denied")
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = principal.getUsername();
                    System.out.println(username + " is denied to access "+request.getRequestURI());
                    response.sendRedirect("/access-denied");
                })
        ;

        //?????? ??????????????? SecurityContextHolder ?????? ??????
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    //WebSecurityConfigurerAdaptor deprecated
    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated();
        http.formLogin();
        http.httpBasic();

        return http.build();
    }*/

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("keesun").password("{noop}123").roles("USER").and()
                .withUser("admin").password("{noop}!@#").roles("ADMIN");
    }*/

    //WebSecurityConfigurerAdaptor deprecated
    /*@Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}123")
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("{noop}123")
                .roles("ADMIN")
                .build();
        UserDetails[] userDetails = new UserDetails[2];
        userDetails[0] = user;
        userDetails[1] = admin;
        return new InMemoryUserDetailsManager(userDetails);
    }*/
}
