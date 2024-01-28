package com.cos.security2.controller;

import com.cos.security2.model.Users;
import com.cos.security2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    @Autowired
    private PasswordEncoder encrypt;
    @Autowired
    private UserRepository userRepository;

//    public RestApiController(BCryptPasswordEncoder encrypt) {
//        this.encrypt = encrypt;
//    }

    @GetMapping("home")
    public String home(){
        return "<h1>Home</h1>";
    }

    @PostMapping("token")
    public String token(){
        return "<h1>Token</h1>";
    }

    @PostMapping("join")
    public String join(@RequestBody Users users){
        users.setPassword(encrypt.encode(users.getPassword()));
        users.setRoles("ROLE_USER");
        userRepository.save(users);

        return "회원가입완료";
    }

    @PostMapping("loginTest")
    public String loginTest(@RequestBody Users users){
        Users byUsername = userRepository.findByUsername(users.getUsername());
        String encryptPassword = byUsername.getPassword();
        System.out.println("encryptPassword +\" , \"+ users.getPassword() = " + encryptPassword +" , "+ users.getPassword());
        boolean result = encrypt.matches(users.getPassword(), encryptPassword);
        System.out.println("결과 = " + result);

        return "loginTest";
    }

    @GetMapping("api/v1/user")
    public String user(){
        return "user";
    }

    @GetMapping("api/v1/manager")
    public String manager(){
        return "manager";
    }

    @GetMapping("api/v1/admin")
    public String admin(){
        return "admin";
    }
}
