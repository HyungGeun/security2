package com.cos.security2.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter3 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("doFilter 시작");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String method = req.getMethod();
        if(method.equals("POST")){
            String authorization = req.getHeader("Authorization");
            // cos : 토큰
            // id, pw 가 정상적으로 들어와서 로그인이 되면 토큰을 만들어주고 이걸 응답을 해야함
            // 요청 할때마다 헤더의 Authorization 의 value 값으로 토큰을 들고 올거다
            // 이 때 들고오는 토큰이 내가 만든 토큰인지 검증만 해주면 된다. RSA, HS256
            if("cos".equals(authorization)){
                chain.doFilter(request, response); // chain 에 요청,응답을 넘겨줌
            } else {
                PrintWriter out = res.getWriter();
                out.println("인증 안됨");
            }
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
