package com.cos.security2.config;

import com.cos.security2.filter.MyFilter3;
import com.cos.security2.jwt.JwtAuthenticationFilter;
import com.cos.security2.jwt.JwtAuthorizationFilter;
import com.cos.security2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터(SecurityConfig.java)가 스프링 필터체인에 등록됨
//prePostEnabled - Spring Security 의 @PreAuthorize, @PreFilter, @PostAuthorize, @PostFilter 어노테이션 활성화 여부
//securedEnabled - @Secured 어노테이션 활성화 여부
//jsr250Enabled - @RoleAllowed 어노테이션 사용 활성화 여부
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    private final CorsConfig corsConfig;



//    @Bean
//    public BCryptPasswordEncoder getPasswordEncoder()
//    {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
//        http.addFilterBefore(new MyFilter3(), SecurityContextHolderFilter.class); // Security Filter 보다 먼저 동작
        http.csrf(cs -> cs.disable());
        http.cors(cs -> cs.disable());
        http.headers(c -> c.frameOptions(f -> f.disable()).disable());
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http
                .sessionManagement(f -> f.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음
                // 필터에 걸리는 애들은 권한 체크를 함. 안걸리는 애들은 다 통과 시킴
                .addFilter(corsFilter) // CrossOrigin(인증 X 정책 안쓸거야+모든 요청 허용할거야), 시큐리티 필터에 등록 인증(O)
                .formLogin(f -> f.disable())
//                .with(MyCustomDsl.customDsl(), (dsl) -> dsl.flag(true)) // 커스텀 필터 등록
                // basic -> http 기본 인증방식. 헤더에 id,pw를 항상 들고 다님.
                // 노출 시 위험이 큼. 그래서 사용안함 설정 해주고
                // 토큰 으로 인증하는 방식을 사용 할거임. (bearer 방식)
//                .addFilter(new JwtAuthenticationFilter(authenticationManager))
//                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository))
                .with(new MyCustomDsl(), (dsl) -> dsl.flag(true))
        .csrf(cs -> cs.disable())
                .httpBasic(f -> f.disable())
                .authorizeHttpRequests(authorize -> {
//                    authorize.requestMatchers("/", "/**").permitAll(); 권한 전체 허용
                    authorize.requestMatchers("/api/v1/user/**").hasAnyRole("USER","MANAGER","ADMIN");
                    authorize.requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER","ADMIN");
                    authorize.requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN");
                    authorize.anyRequest().permitAll();
                });

        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        private boolean flag;
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
        }

        public MyCustomDsl flag(boolean value) {
            this.flag = value;
            return this;
        }
    }
}
