package com.ssafy.ssafvey.global.config.jwt;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class JwtFilter extends GenericFilterBean {
   // logger 선언
   private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
   // ACCESS_HEADER 선언
   public static final String ACCESS_HEADER = "Authorization";
   // REFRESH_HEADER 선언
   public static final String REFRESH_HEADER = "refreshToken";
   private TokenProvider tokenProvider;
   public JwtFilter(TokenProvider tokenProvider) {
      this.tokenProvider = tokenProvider;
   }

   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//      logger.debug("Jwt filter start ");
      //  JWT 토큰을 추출하고, 추출한 토큰이 유효한지 검증
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      String jwt = resolveToken(httpServletRequest);
      String requestURI = httpServletRequest.getRequestURI();

      log.info("====================================================");
      log.info("요청 IP ADDRESS : {}", servletRequest.getRemoteAddr());
      log.info("요청 URI : {}", requestURI);
      if (StringUtils.hasText(jwt)){
         if(tokenProvider.validateToken(jwt)){ // 토큰이 유효하다면
            Authentication authentication = tokenProvider.getAuthentication(jwt); // Authentication 객체(권한 정보들)를 가져온다.
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            Long memberId = (long) Integer.parseInt(username);
            servletRequest.setAttribute("memberId", memberId);
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 set한다.
            logger.info("MEMBER ADDRESS IN TOKEN : '{}'", authentication.getName());
            logger.info("JWT 토큰이 유효합니다.");
         } else {
            // 토큰 재발급 요청 메소드 차후 개선
            logger.info("JWT 토큰이 유효하지 않습니다.");
         }
      }
      filterChain.doFilter(servletRequest, servletResponse);
   }

   private String resolveToken(HttpServletRequest request) { // request header에서 token 정보를 가져옴
      String bearerToken = request.getHeader(ACCESS_HEADER);
      // ACCESS_HEADER 상수로 정의된 문자열을 사용하여 HTTP request header에서 "Bearer "로 시작하는 Authorization 헤더를 검색합니다.
      // 검색된 문자열이 null이 아니며, "Bearer "로 시작한다면 실제 인증 토큰 정보를 추출하여 반환하고, 그렇지 않다면 null을 반환합니다.
      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
         return bearerToken.substring(7);
      }

      return null;
   }
}
