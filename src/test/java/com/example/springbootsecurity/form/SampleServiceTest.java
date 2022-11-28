package com.example.springbootsecurity.form;

import com.example.springbootsecurity.account.Account;
import com.example.springbootsecurity.account.AccountContext;
import com.example.springbootsecurity.account.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SampleServiceTest {

    @Autowired
    SampleService sampleService;

    @Autowired
    AccountService accountService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Test
    @WithMockUser
    public void dashboard(){
        Account account = new Account();

        //account.setRole("USER");
        account.setRole("ADMIN");
        account.setUsername("keesun");
        account.setPassword("123");
        accountService.createNew(account);

        UserDetails userDetails = accountService.loadUserByUsername("keesun");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails , "123");
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        sampleService.dashboard();
    }

    @Test
    @WithMockUser
    public void dashboard2(){
        sampleService.dashboard();
    }

}