package com.ssafy.ssafvey.global.config;


import com.ssafy.ssafvey.global.config.jwt.JwtSecurityConfig;
import com.ssafy.ssafvey.global.config.jwt.JwtFilter;
import com.ssafy.ssafvey.global.config.jwt.TokenProvider;
import com.ssafy.ssafvey.global.config.jwt.JwtAccessDeniedHandler;
import com.ssafy.ssafvey.global.config.jwt.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity // 기본적인 웹 보안을 활성화하겠다는 의미이다.
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    // TokenProvider 선언
    private final TokenProvider tokenProvider;
    // CorsFilter 선언
    private final CorsFilter corsFilter;
    // JwtAuthenticationEntryPoint 선언
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    // JwtAccessDeniedHandler 선언
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(TokenProvider tokenProvider, CorsFilter corsFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        // TokenProvider는 JWT 토큰 생성과 유효성 검증을 담당
        this.tokenProvider = tokenProvider;
        // CorsFilter는 Cross-Origin Resource Sharing(CORS)를 처리하기 위한 필터
        this.corsFilter = corsFilter;
        // JwtAuthenticationEntryPoint는 인증되지 않은 요청에 대한 처리를 담당
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        // JwtAccessDeniedHandler는 권한이 없는 요청에 대한 처리를 담당
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }


    @Bean
    //  해시 함수를 사용하여 비밀번호를 안전하게 저장하기 위한 패스워드 인코더
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                // exception handler
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // enable h2-console
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근 제한을 설정하겠다는 의미
                .antMatchers("/authenticate","/v2/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                .antMatchers("/api/member/login**","/api/member/refresh","/api/survey/list**","/api/survey/start/**","/api/member/jobs","/api/shop/items/**","/api/member/tmpToken").permitAll()
                // 인증된 사용자만 접근
                .antMatchers("/api/**").authenticated()
                // 누구나 접근 가능하도록 설정
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().permitAll()
                //  CORS(Cross-Origin Resource Sharing)를 허용
                .and()
                .cors()
                //  JWT를 사용한 인증을 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
        return http.build();
    }

    // "/h2-console/**"와 "/favicon.ico" URL에 대한 요청은 Spring Security의 보안 검사를 거치지 않으며 무시
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/h2-console/**"
                ,"/favicon.ico");
    }

}
