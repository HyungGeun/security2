package com.cos.security2.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MyFilter1 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

//        MyFilter3 에 했음
//        String headerAuth = req.getHeader("Authorization");
//        System.out.println("headerAuth = " + headerAuth);
//        System.out.println("MyFilter1 시작");

        chain.doFilter(req, res); // chain 에 요청,응답을 넘겨줌
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
