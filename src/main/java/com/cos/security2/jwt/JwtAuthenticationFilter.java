package com.cos.security2.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.security2.auth.PrincipalDetails;
import com.cos.security2.model.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter 가 있음
// /login 요청해서 username, password 전송하면(POST)
// UsernamePasswordAuthenticationFilter 가 동작을 함
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

//    /login 요청을 하면 로그인 시도를 하기 위해서 호출되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter.attemptAuthentication 로그인 시도중");

        ObjectMapper om = new ObjectMapper();
        try {
            // 1. username, password를 받아서
            Users users = om.readValue(request.getInputStream(), Users.class);

            System.out.println("users.getUsername() + users.getPassword() = " + users.getUsername() + users.getPassword());

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword());

            // 2. 정상인지 로그인 시도를 함. authenticationManager 으로 로그인 시도를 하면
            // PrincipalDetailsService 의 loadUserByUsername() 가 실행 됨
            // 정상 로그인 일시 authentication 객체가 리턴 됨
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // authentication 객체가 session 영역에 저장 됨 => 정상 로그인이 되었다는 뜻
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료 = " + principalDetails.getUsers().getUsername());

            // authentication 객체가 세션에 저장 됨
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        // 3. PrincipalDetails 를 세션에 담고 (권한 관리를 위해서)

        // JWT 토크을 만들어서 응답 해주면 됨
    }

    // attemptAuthentication 로그인이 정상적으로 완료 되면 successfulAuthentication 가 실행됨
    // 이 곳에서 JWT 토큰을 만들어서 request 한 사용자에게 response 해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication : 로그인이 성공했음");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        
        // RSA 방식은 아니고 Hash 암호방식
        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10))) // 토큰 만료시간 10분
                .withClaim("username", principalDetails.getUsers().getUsername())
                .sign(Algorithm.HMAC512("cos"));

        response.addHeader("Authorization", "Bearer "+jwtToken);
//        super.successfulAuthentication(request, response, chain, authResult);
    }
}
