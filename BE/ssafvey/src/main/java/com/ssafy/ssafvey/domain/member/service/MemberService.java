package com.ssafy.ssafvey.domain.member.service;


import com.ssafy.ssafvey.domain.member.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MemberService {
    LoginItem getKakaoAccessToken(String code);

    Long getMemberId(HttpServletRequest request);
    void updateUser(Long id, SignUpRequestDto signUpRequestDto);

    void logout(String accessToken);

    MypageResponseDto getMypage(Long id);

    Map<String,Object> refreshAccessToken(String refreshToken);

    JobListResponseDto getJobs();

    Map<String, List<RecentItem>> getSurveyParticipated(Long id);

    Map<String, List<RecentItem>> getSurveyCreated(Long id);

    int getPoint(Long id);

    Map<String,Object> tmpAccessToken(Long id);
}
