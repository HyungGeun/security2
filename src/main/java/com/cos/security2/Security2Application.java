package com.cos.security2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Security2Application {

	// BCryptPasswordEncoder 를 IOC로 등록. 로그인 시 스프링에서 기본으로 IOC에 등록 된 이 암호화 방식을
	// 선택해서 로그인을 시도함
	// PasswordEncoder IOC가 안돼...  There is no PasswordEncoder mapped for the id "null"
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(Security2Application.class, args);
	}

}
