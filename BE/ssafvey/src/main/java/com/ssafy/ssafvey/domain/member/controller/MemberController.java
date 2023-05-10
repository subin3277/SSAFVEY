package com.ssafy.ssafvey.domain.member.controller;



import com.ssafy.ssafvey.domain.member.dto.*;
import com.ssafy.ssafvey.domain.member.service.MemberService;
import com.ssafy.ssafvey.global.config.jwt.JwtFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "사용자 관련 기능 API")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @ApiOperation(value="카카오로그인", notes = "카카오 로그인을 합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @GetMapping("/api/member/login")
    public ResponseEntity kakaoLogin(@RequestParam String code) {
        LoginItem loginItem = memberService.getKakaoAccessToken(code);
//        LoginResponseDto loginResponseDto = LoginResponseDto.toLoginResponse(loginItem);
        return ResponseEntity.status(HttpStatus.OK)
//                .headers(returnTokenHeader(loginItem.getToken()))
                .body(loginItem);
    }

    @ApiOperation(value="직업리스트", notes = "직업 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @GetMapping("/api/member/jobs")
    public ResponseEntity jobList() {
        JobListResponseDto jobListResponseDto = memberService.getJobs();
        return ResponseEntity.status(HttpStatus.OK)
                .body(jobListResponseDto);
    }

    @ApiOperation(value="로그아웃", notes = "헤더의 access 토큰 정보를 통해 refreshToken을 삭제 시킨다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(로그아웃 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @GetMapping(path="/api/member/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // jwt토큰값을 받습니다.
        String accessToken = request.getHeader(JwtFilter.ACCESS_HEADER);
        // logout(토큰값 제거)
        memberService.logout(accessToken.substring(7));

        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 완료");
    }

    @ApiOperation(value="유저 업데이트", notes = "유저 정보를 업데이트 합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @PutMapping("/api/member/changeProfil")
    public ResponseEntity updateUser(HttpServletRequest request, @RequestBody SignUpRequestDto signUpRequestDto ) {
        memberService.updateUser((Long) request.getAttribute("memberId"),signUpRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body("저장 완료");
    }



    @ApiOperation(value = "Access 토큰 재발급", notes = "헤더의 refresh 토큰 정보를 통해 access 토큰을 재발급한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Access Token 재발급 성공"),
            @ApiResponse(code = 400, message = "기타 오류"),
            @ApiResponse(code = 401, message = "UNAUTHORIZED(재발급 실패, 로그아웃)"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    @GetMapping("/api/member/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        // refreshAccessToken을 조회해서 재생성
        String refreshToken = request.getHeader(JwtFilter.REFRESH_HEADER);
        Map<String, Object> result = memberService.refreshAccessToken(refreshToken);
//        HttpHeaders headers = returnTokenHeader(result);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    @ApiOperation(value="마이페이지", notes = "유저 정보를 업데이트 합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @GetMapping("/api/member/mypage")
    public ResponseEntity getMypage(HttpServletRequest request) {
        MypageResponseDto mypageResponseDto = memberService.getMypage((Long) request.getAttribute("memberId"));

        return ResponseEntity.status(HttpStatus.OK).body(mypageResponseDto);
    }

    @ApiOperation(value="참가한 설문", notes = "유저 정보를 업데이트 합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @GetMapping("/api/member/mypage/surveyParticipated")
    public ResponseEntity getParticipated(HttpServletRequest request) {
        Map<String, List<RecentItem>> surveysResponseDto = memberService.getSurveyParticipated((Long) request.getAttribute("memberId"));

        return ResponseEntity.status(HttpStatus.OK).body(surveysResponseDto);
    }

    @ApiOperation(value="작성한 설문", notes = "유저 정보를 업데이트 합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @GetMapping("/api/member/mypage/surveyCreated")
    public ResponseEntity getCreated(HttpServletRequest request) {
        Map<String, List<RecentItem>> surveysResponseDto = memberService.getSurveyCreated((Long) request.getAttribute("memberId"));

        return ResponseEntity.status(HttpStatus.OK).body(surveysResponseDto);
    }

    //TODO 이건 꼭 지울 것
    @ApiOperation(value="tmp 엑세스토큰", notes = "유저 정보를 업데이트 합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @GetMapping("/api/member/tmpToken")
    public ResponseEntity tmpToken(@RequestParam Long id) {

        Map<String, Object> token = memberService.tmpAccessToken(id);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    
    @ApiOperation(value="로또 뜯기", notes = "유저 정보를 업데이트 합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK(회원 가입 성공)"),
            @ApiResponse(code = 400, message = "BAD REQUEST(요청 실패)"),
            @ApiResponse(code = 500, message = "서버에러")
    })
    @PutMapping("/api/member/mypage/lotto")
    public ResponseEntity useLotto(HttpServletRequest request) {


        return ResponseEntity.status(HttpStatus.OK).body(memberService.getPoint((Long) request.getAttribute("memberId")));
    }


    public HttpHeaders returnTokenHeader(Map<String, Object> result) {
        //  HTTP 요청 헤더에 액세스 토큰과 리프레시 토큰을 추가하여 인증에 필요한 정보를 제공
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtFilter.ACCESS_HEADER, (String) result.get("Authorization"));
        headers.add(JwtFilter.REFRESH_HEADER, (String) result.get("refreshToken"));

        return headers;
    }

}
