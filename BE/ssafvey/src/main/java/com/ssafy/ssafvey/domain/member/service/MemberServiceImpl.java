package com.ssafy.ssafvey.domain.member.service;

//import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.JsonElement;
import com.ssafy.ssafvey.domain.member.dto.*;
import com.ssafy.ssafvey.domain.member.entity.*;
import com.ssafy.ssafvey.domain.member.exception.BadRequestException;
import com.ssafy.ssafvey.domain.member.exception.UnAuthorizationException;
import com.ssafy.ssafvey.domain.member.repository.JobsRepository;
import com.ssafy.ssafvey.domain.member.repository.MemberJobRepository;
import com.ssafy.ssafvey.domain.member.repository.MemberRepository;
import com.ssafy.ssafvey.domain.member.repository.PointHistoryRepository;
import com.ssafy.ssafvey.domain.shop.entity.Order;
import com.ssafy.ssafvey.domain.survey.entity.Survey;
import com.ssafy.ssafvey.domain.survey.repository.SurveyRepository;
import com.ssafy.ssafvey.global.config.jwt.JwtFilter;
import com.ssafy.ssafvey.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.gson.JsonParser;


import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    // MemberRepository 선언
    private final MemberRepository memberRepository;

    private final SurveyRepository surveyRepository;

    private final JobsRepository jobsRepository;

    private final MemberJobRepository memberJobRepository;
    // TokenProvider 선언
    private final TokenProvider tokenProvider;

    private final PointHistoryRepository pointHistoryRepository;


    public void updateUser(Long id, SignUpRequestDto signUpRequestDto){
        Optional<Member> findMember = memberRepository.findById(id);
        List<MemberJob> jobList = new ArrayList<>();
        if (findMember.isPresent()) {
            if (findMember.get().getMemberJobs().isEmpty()) {
            }
            else{
                for (MemberJob memberJob : findMember.get().getMemberJobs()) {
                    Long tmpId = memberJob.getId();
                    memberJobRepository.deleteById(tmpId);
                }
            }
        }
        for (Long job : signUpRequestDto.getJobs()) {
            MemberJob memberJob = new MemberJob();
            Job findJob = jobsRepository.findById(job).get();;
            memberJob.setMember(findMember.get());
            memberJob.setJob(findJob);
            jobList.add(memberJob);
            memberJobRepository.save(memberJob);
        }
        memberRepository.save(signUpRequestDto.tomember(findMember.get(),id,signUpRequestDto,jobList));
    }




    @Override
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        // getMemberInfoWithToken(refreshToken) 메서드를 호출하여, refreshToken을 기반으로 회원 정보를 조회합니다.
        Optional<Member> object = getMemberInfoWithToken(refreshToken);
        if (object != null) {
            Member member = object.get();
            if (refreshToken.equals(member.getRefreshToken())) {
                // 메서드를 사용하여 refreshToken이 유효한지 검증합니다. 이 검증 과정에서 refreshToken이 만료되었거나, 서명이 잘못되었거나 등의 이유로 검증에 실패하면 UnAuthorizationException을 발생시켜 예외 처리합니다.
                if (tokenProvider.validateToken(refreshToken)) {
                    // 메서드를 호출하여 회원 정보를 기반으로 새로운 토큰을 발급합니다.
                    HashMap<String, Object> token = returnToken(member);
                    // 회원 객체의 refreshToken 값을 새로운 액세스 토큰으로 갱신
                    member.setRefreshToken((String) token.get(JwtFilter.REFRESH_HEADER));
                    // 토큰 갱신
                    memberRepository.save(member);
                    // token 맵을 반환
                    return token;
                } else {
                    throw new UnAuthorizationException("refreshToken 만료");
                }
            } else {
                throw new UnAuthorizationException("refreshToken 매칭 오류");
            }
        } else {
            throw new BadRequestException("회원이 존재 하지 않습니다.");
        }
    }

    @Override
    public void logout(String accessToken) {
        //  accessToken을 기반으로 회원 정보를 조회합니다
        Optional<Member> member = getMemberInfoWithToken(accessToken);

        // 회원의 refreshToken을 빈 문자열로 갱신
        member.get().setRefreshToken("");
        memberRepository.save(member.get());

    }

    public Long getMemberId(HttpServletRequest request){
        // HTTP 요청에서 ACCESS_HEADER에 해당하는 값을 추출
        String accessToken = request.getHeader(JwtFilter.ACCESS_HEADER);
        // 액세스 토큰 문자열에서 "Bearer " 문자열을 제거하고, 나머지 액세스 토큰 문자열을 인자로 전달
        Authentication authentication = tokenProvider.getAuthentication(accessToken.substring(7));
        // 회원주소 반환
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long memberId = (long) Integer.parseInt(username);

        return memberId;
    }

    @Transactional
    public LoginItem getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=08464b27e8d442e6bde11f4a08656b53"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:5173/onlylogin"); // TODO 인가코드 받은 redirect_uri 입력
//            sb.append("&redirect_uri=https://k8a608.p.ssafy.io/onlylogin"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoginItem loginItem = createKakaoUser(access_Token);
        return loginItem;
    }
    public LoginItem createKakaoUser(String token)  {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
//            boolean hasAgeRange = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_age_range").getAsBoolean();
            boolean hasGender = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_gender").getAsBoolean();
            String email = "";
            String nickname = "";
            String gender = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
//            if(hasAgeRange){

            nickname = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();
//            }
            if(hasGender){
                gender = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("gender").getAsString();
            }

            // 로그인하였을 때 DB에 등록된 유저인지 확인
            if (memberRepository.findByEmail(email) != null) {
                Member findMember = memberRepository.findByEmail(email);
                Map<String, Object> resultToken = returnToken(findMember);
                LoginItem loginItem = LoginItem.builder()
                        .name(findMember.getName())
                        .email(findMember.getEmail())
                        .genderType(findMember.getGenderType())
                        .isRegistered(findMember.getIsRegistered())
                        .token(resultToken)
                        .build();
                return loginItem;
            }
            else{
                Map<String, Object> resultToken = signUpMember(email,nickname,gender);
                Member findMember = memberRepository.findByEmail(email);
                LoginItem loginItem = LoginItem.builder()
                        .name(findMember.getName())
                        .email(findMember.getEmail())
                        .genderType(findMember.getGenderType())
                        .isRegistered(findMember.getIsRegistered())
                        .token(resultToken)
                        .build();
                return loginItem;
            }


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> signUpMember(String email,String nickname, String gender) {

        Authority userAuthority = Authority.user();
        GenderType isGender;
        if (gender.equals("male")) {
            isGender=GenderType.MAN;
        }
        else{
            isGender=GenderType.WOMAN;
        }


        // 맴버 객체에 저장
        Member member = Member.builder()
                .email(email)
                .name(nickname)
                .genderType(isGender)
                .authorities(Set.of(userAuthority))
                .isRegistered(false)
                .build();

        memberRepository.save(member);


        // 토큰 발급 result 리턴
        Map<String, Object> result = returnToken(member);
        return result;
    }

    public MypageResponseDto getMypage(Long id){
        Member findMember = memberRepository.findById(id).get();



        List<MemberSurvey> memberSurveys = findMember.getMemberSurveys();
        Long numCreated = 0L;
        Long numParticipated = 0L;
        // date 필드를 기준으로 비교하는 Comparator 객체 생성
        Comparator<MemberSurvey> compareByDate = new Comparator<MemberSurvey>() {
            @Override
            public int compare(MemberSurvey s1, MemberSurvey s2) {
                return s2.getCreatedAt().compareTo(s1.getCreatedAt());
            }
        };

        // MemberSurvey 객체의 리스트를 date 필드를 기준으로 정렬
        Collections.sort(memberSurveys, compareByDate);

        // 최신 3개의 객체 가져오기
        List<MemberSurvey> latest3Surveys = memberSurveys.subList(0, Math.min(3, memberSurveys.size()));

        for (MemberSurvey memberSurvey : memberSurveys) {
            // 각 memberSurvey에 대한 작업 수행
            if (memberSurvey.getIsOwner()) {
                numCreated++;
            }else {
                // isOwner가 false일 때 실행할 코드
                numParticipated++;
            }
        }

        return MypageResponseDto.mypageResponseDto(findMember,numCreated,numParticipated,latest3Surveys);

    }

    public Map<String, List<RecentItem>> getSurveyParticipated(Long id){
        Member findMember = memberRepository.findById(id).get();
        List<MemberSurvey> participatedMemberSurvey = new ArrayList<>();
        List<MemberSurvey> memberSurveys = findMember.getMemberSurveys();
        for (MemberSurvey memberSurvey : memberSurveys) {
            // 각 memberSurvey에 대한 작업 수행
            if (!memberSurvey.getIsOwner()) {
                participatedMemberSurvey.add(memberSurvey);
            }
        }


        return SurveysResponseDto.getSurveyParticipated(findMember,participatedMemberSurvey);
    }

    public Map<String, List<RecentItem>> getSurveyCreated(Long id){
        Member findMember = memberRepository.findById(id).get();
        List<MemberSurvey> createdMemberSurvey = new ArrayList<>();
        List<MemberSurvey> memberSurveys = findMember.getMemberSurveys();
        for (MemberSurvey memberSurvey : memberSurveys) {
            // 각 memberSurvey에 대한 작업 수행
            if (memberSurvey.getIsOwner()) {
                createdMemberSurvey.add(memberSurvey);
            }
        }

        return SurveysResponseDto.getSurveyCreated(findMember,createdMemberSurvey);
    }

    public int getPoint(Long id){
        Member findMember = memberRepository.findById(id).get();
        if(findMember.getCouponCount()==0){
            throw new BadRequestException("쿠폰이 존재하지 않습니다.");
        }
        findMember.setCouponCount(findMember.getCouponCount()-1);
        Random random = new Random();
        int minNum = 1;
        int maxNum = 100;
        int point = 0;
        int randomPoint = random.nextInt(maxNum - minNum + 1) + minNum;
        if (randomPoint == 1) {
            point=1000;
        } else if (randomPoint >= 2 && randomPoint <= 8) {
            point=800;

        }else if (randomPoint >= 9 && randomPoint <= 18) {
            point=600;

        }else if (randomPoint >= 19 && randomPoint <= 28) {
            point=500;

        }else if (randomPoint >= 29 && randomPoint <= 45) {
            point=400;
            // 11부터 20까지의 경우 처리할 내용
        }else if (randomPoint >= 46 && randomPoint <= 75) {
            point=300;

        }
        else {
            point=100;

        }


        findMember.setPoint(findMember.getPoint()+point);
        memberRepository.save(findMember);
        PointHistory pointHistory = new PointHistory();
        pointHistory.setPoint(point);
        pointHistory.setMember(findMember);
        pointHistory.setPointUsageHistory("로또 사용");
        pointHistory.setPlusMinus(true);
        pointHistory.setCreateDate(LocalDateTime.now());
        pointHistoryRepository.save(pointHistory);


        return point;
    }

    public Map<String, List<PointResponseDto>> getMypagePoint(Long id){
        Member findMember = memberRepository.findById(id).get();

        List<PointHistory> pointHistoryList = findMember.getPointHistories();

        pointHistoryList.sort(Comparator.comparing(PointHistory::getCreateDate).reversed());


        List<PointResponseDto> pointResponseDtoList = new ArrayList<>();

        for(PointHistory tmpPointHistory : pointHistoryList){
            PointResponseDto pointResponseDto = new PointResponseDto();
            pointResponseDto.setPointUsageHistory(tmpPointHistory.getPointUsageHistory());
            pointResponseDto.setPoint(tmpPointHistory.getPoint());
            pointResponseDto.setPlusMinus(tmpPointHistory.getPlusMinus());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String formattedDate =tmpPointHistory.getCreateDate().format(formatter);
            pointResponseDto.setDate(formattedDate);
            pointResponseDtoList.add(pointResponseDto);
        }


        // PointHistory 객체 리스트를 날짜(date)별로 그룹화하여 Map 객체에 저장
        Map<String, List<PointResponseDto>> result = pointResponseDtoList.stream()
                .collect(Collectors.groupingBy(PointResponseDto::getDate));

        return result;
    }

    public MemberPointResponseDto getMemberPoint(Long id){
        Member findMember = memberRepository.findById(id).get();
        MemberPointResponseDto memberPointResponseDto = new MemberPointResponseDto();
        memberPointResponseDto.setPoint(findMember.getPoint());

        return memberPointResponseDto;
    }

    public Map<String,Object> tmpAccessToken(Long id){
        Member findMember = memberRepository.findById(id).get();
        HashMap<String, Object> token = returnToken(findMember);

        return token;
    }

    public HashMap<String, Object> returnToken(Member member) {
        //  사용자 정보(member)를 기반으로 Access Token을 생성합니다. 사용자 정보를 기반으로 Refresh Token을 생성합니다.
        String Authorization = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member);

        // 변경된 사용자 정보를 DB에 저장합니다.
        member.setRefreshToken(refreshToken);


        //  put 메서드를 사용하여 Access Token과 Refresh Token 값을 key-value 형태로 저장한 뒤, 이를 반환합니다.
        return new HashMap<>() {{
            put(JwtFilter.ACCESS_HEADER, Authorization);
            put(JwtFilter.REFRESH_HEADER, refreshToken);
        }};
    }

    public Optional<Member> getMemberInfoWithToken(String token) {
        // 주어진 토큰(token)을 이용하여 인증(authentication)을 수행합니다. 이때, 인증된 사용자 정보는 authentication 객체로 반환됩니다.
        Authentication authentication = tokenProvider.getAuthentication(token);
        // 인증된 사용자의 주소를 이용하여 DB에서 사용자 정보를 찾아 반환합니다.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long memberId = (long) Integer.parseInt(username);
        return memberRepository.findById(memberId);
    }

    public MemberResponseDto getMeberInfo(Long id){
        Member findMember = memberRepository.findById(id).get();

        return MemberResponseDto.memberResponseDto(findMember);
    }



    public JobListResponseDto getJobs(){
        JobListResponseDto jobListResponseDto = new JobListResponseDto();
        List<JobItem> jobs = new ArrayList<>();
        jobs.add(new JobItem(1, "전문직"));
        jobs.add(new JobItem(2, "경영/관리직"));
        jobs.add(new JobItem(3, "사무/기술직"));
        jobs.add(new JobItem(4, "판매/서비스직"));
        jobs.add(new JobItem(5, "기능/작업/단순노무직"));
        jobs.add(new JobItem(6, "농림어축산업"));
        jobs.add(new JobItem(7, "자영업"));
        jobs.add(new JobItem(8, "전업주부"));
        jobs.add(new JobItem(9, "학생"));
        jobs.add(new JobItem(10, "무직"));
        jobs.add(new JobItem(11, "기타"));
        jobListResponseDto.setJobs(jobs);
        return jobListResponseDto;
    }

}
