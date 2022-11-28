package com.example.springbootsecurity.form;

import com.example.springbootsecurity.account.Account;
import com.example.springbootsecurity.account.AccountContext;
import com.example.springbootsecurity.account.AccountRepository;
import com.example.springbootsecurity.book.Book;
import com.example.springbootsecurity.book.BookRepository;
import com.example.springbootsecurity.common.CurrentUser;
import com.example.springbootsecurity.common.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.Callable;

@Controller
public class SampleController {

    @Autowired SampleService sampleService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    BookRepository bookRepository;

    @GetMapping("/")
    //public String index(Model model , Principal principal){
    //public String index(Model model , @AuthenticationPrincipal UserAccount userAccount){
    public String index(Model model , @CurrentUser Account account){
        if(account ==null){
            model.addAttribute("message","Hello Spring Security");
        }else {
            model.addAttribute("message","Hello "+account.getUsername());
        }

        return "index";
    }

    @GetMapping("/info")
    public String info(Model model){
        model.addAttribute("message","Hello Spring Security");
        return "info";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model , Principal principal){
        model.addAttribute("message","Hello "+principal.getName());
        AccountContext.setAccount(accountRepository.findByUsername(principal.getName()));
        sampleService.dashboard();

        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model , Principal principal){
        model.addAttribute("message","Hello admin" + principal.getName());
        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model , Principal principal){
        model.addAttribute("message","Hello user " + principal.getName());
        List<Book> list = bookRepository.findCurrentUserBooks();
        model.addAttribute("books" , bookRepository.findCurrentUserBooks());

        return "user";
    }

    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> asyncHandler(){
        SecurityLogger.log("MVC");
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                SecurityLogger.log("Callable");
                return "Async Handler";
            }
        };
    }

    @GetMapping("/async-service")
    @ResponseBody
    public String asyncService(){
        SecurityLogger.log("MVC before async service");
        sampleService.asyncService();
        SecurityLogger.log("MVC after async service");
        return "Async Service";
    }
}
