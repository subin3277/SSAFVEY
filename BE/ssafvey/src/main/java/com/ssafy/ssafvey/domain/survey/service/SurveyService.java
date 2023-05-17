package com.ssafy.ssafvey.domain.survey.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.ssafvey.domain.member.entity.*;
import com.ssafy.ssafvey.domain.member.repository.MemberAnswerDescriptiveRepository;
import com.ssafy.ssafvey.domain.member.repository.MemberAnswerMultipleChoiceRepository;
import com.ssafy.ssafvey.domain.member.repository.MemberRepository;
import com.ssafy.ssafvey.domain.member.repository.MemberSurveyRepository;
import com.ssafy.ssafvey.domain.member.service.MemberAnswerServiceImpl;
import com.ssafy.ssafvey.domain.member.service.MemberSurveyService;
import com.ssafy.ssafvey.domain.survey.dto.*;
import com.ssafy.ssafvey.domain.survey.dto.request.*;
import com.ssafy.ssafvey.domain.survey.entity.*;
import com.ssafy.ssafvey.domain.survey.repository.*;
import com.ssafy.ssafvey.utils.Publisher;
import com.ssafy.ssafvey.utils.UUIDGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionService surveyQuestionService;
    private final SurveyTargetAgeRepository surveyTargetAgeRepository;
    private final SurveyTargetJobRepository surveyTargetJobRepository;

    private final JobRepository jobRepository;
    private final MemberAnswerServiceImpl memberAnswerServiceImpl;
    private final MemberSurveyService memberSurveyService;
    private final MemberRepository memberRepository;
    private final MemberSurveyRepository memberSurveyRepository;

    private final ModelMapper modelMapper;
    private final MemberAnswerMultipleChoiceRepository memberAnswerMultipleChoiceRepository;
    private final MemberAnswerDescriptiveRepository memberAnswerDescriptiveRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final ObjectMapper objectMapper;
    private final SurveyStatisticsRepository surveyStatisticsRepository;
    private final Publisher publisher;

    public SurveyService(SurveyRepository surveyRepository, SurveyQuestionService surveyQuestionService, SurveyTargetAgeRepository surveyTargetAgeRepository, SurveyTargetJobRepository surveyTargetJobRepository, JobRepository jobRepository, MemberAnswerServiceImpl memberAnswerServiceImpl, MemberSurveyService memberSurveyService, MemberRepository memberRepository, MemberSurveyRepository memberSurveyRepository, ModelMapper modelMapper, MemberAnswerMultipleChoiceRepository memberAnswerMultipleChoiceRepository, MemberAnswerDescriptiveRepository memberAnswerDescriptiveRepository, SurveyQuestionRepository surveyQuestionRepository, ObjectMapper objectMapper, SurveyStatisticsRepository surveyStatisticsRepository, Publisher publisher) {
        this.surveyRepository = surveyRepository;
        this.surveyQuestionService = surveyQuestionService;
        this.surveyTargetAgeRepository = surveyTargetAgeRepository;
        this.surveyTargetJobRepository = surveyTargetJobRepository;
        this.jobRepository = jobRepository;
        this.memberAnswerServiceImpl = memberAnswerServiceImpl;
        this.memberSurveyService = memberSurveyService;
        this.memberRepository = memberRepository;
        this.memberSurveyRepository = memberSurveyRepository;
        this.modelMapper = modelMapper;
        this.memberAnswerMultipleChoiceRepository = memberAnswerMultipleChoiceRepository;
        this.memberAnswerDescriptiveRepository = memberAnswerDescriptiveRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.objectMapper = objectMapper;
        this.surveyStatisticsRepository = surveyStatisticsRepository;
        this.publisher = publisher;
    }

    private SurveyTargetAge createTargetAge(TargetAgeDto targetAgeDto, Survey survey) {
        SurveyTargetAge surveyTargetAge = SurveyTargetAge.builder()
                .minAge(targetAgeDto.getMinAge())
                .maxAge(targetAgeDto.getMaxAge())
                .survey(survey)
                .build();
        return surveyTargetAgeRepository.save(surveyTargetAge);
    }
    private List<SurveyTargetJob> createSurveyTargetJobs(List<Long> targetJobIds, Survey survey) {
        List<SurveyTargetJob> surveyTargetJobs = new ArrayList<>();

        List<Job> jobs = jobRepository.findAllById(targetJobIds);
        for (Job job : jobs) {
            SurveyTargetJob surveyTargetJob = SurveyTargetJob.builder()
                    .survey(survey)
                    .job(job)
                    .build();
            surveyTargetJobs.add(surveyTargetJob);
        }
        return surveyTargetJobs;
    }
    private List<SurveyTargetAge> createSurveyTargetAgeList(List<TargetAgeDto> targetAgeDtoList, Survey survey) {
        List<SurveyTargetAge> surveyTargetAges = new ArrayList<>();

        for (TargetAgeDto targetAgeDto : targetAgeDtoList) {
            SurveyTargetAge surveyTargetAge = createTargetAge(targetAgeDto, survey);
            surveyTargetAges.add(surveyTargetAge);
        }
        return surveyTargetAges;
    }

    @Transactional
    public Survey createSurvey(Long memberId, SurveyDto surveyDto) {
        //TODO member_survey에 created at 값 넣어야함
        Survey survey = Survey.builder()
                .isDone(false)
                .title(surveyDto.getTitle())
                .targetSurveyParticipants(surveyDto.getTargetSurveyParticipants())
                .targetGender(surveyDto.getTargetGender())
                .endDate(surveyDto.getEndDate())
                .organization(surveyDto.getOrganization())
                .description(surveyDto.getDescription())
                .build();
        List<SurveyTargetJob> surveyTargetJobs = createSurveyTargetJobs(surveyDto.getTargetJob(), survey);
        List<SurveyTargetAge> surveyTargetAges = createSurveyTargetAgeList(surveyDto.getTargetAge(), survey);
        List<SurveyQuestion> surveyQuestions = new ArrayList<>();
        List<MemberSurvey> memberSurveys = new ArrayList<>();

        MemberSurvey memberSurvey = memberSurveyService.createMemberSurvey(memberId,survey,true);
        memberSurveys.add(memberSurvey);
        for (SurveyQuestionDto surveyQuestionDto : surveyDto.getSurveyQuestions()) {
            System.out.println(surveyQuestionDto);
            SurveyQuestion surveyQuestion = surveyQuestionService.createSurveyQuestion(surveyQuestionDto, survey);
            surveyQuestions.add(surveyQuestion);
        }

        survey.setSurveyTargetJobs(surveyTargetJobs);
        survey.setSurveyTargetAges(surveyTargetAges);
        survey.setSurveyQuestions(surveyQuestions);
        survey.setMemberSurveys(memberSurveys);

        return surveyRepository.save(survey);
    }

    public StartSurveyDto getStartSurveyById(Object memberId, Long surveyId) {
        Optional<Survey> optionalSurvey = surveyRepository.findById(surveyId);
        if (optionalSurvey.isPresent()) {
            Survey survey = optionalSurvey.get();
            StartSurveyDto startSurveyDto = StartSurveyDto.builder()
                    .id(survey.getId())
                    .surveyParticipants(survey.getSurveyParticipants())
                    .title(survey.getTitle())
                    .isDone(survey.getIsDone())
                    .description(survey.getDescription())
                    .organization(survey.getOrganization())
                    .endDate(survey.getEndDate())
                    .targetSurveyParticipants(survey.getTargetSurveyParticipants())
                    .build();
            if (memberId != null) {
                Member member = memberRepository.findById((Long) memberId).get();
                Optional<MemberSurvey> optionalMemberSurvey = memberSurveyRepository.findByMemberAndSurvey(member, survey);
                if (optionalMemberSurvey.isPresent()) {
                    MemberSurvey memberSurvey = optionalMemberSurvey.get();
                    startSurveyDto.setHaveDone(true);
                    startSurveyDto.setIsAuthor(memberSurvey.getIsOwner());
                } else {
                    startSurveyDto.setHaveDone(false);
                    startSurveyDto.setIsAuthor(false);
                }
            } else {
                startSurveyDto.setHaveDone(false);
                startSurveyDto.setIsAuthor(false);
            }
            return startSurveyDto;
        }

        else {
            //TODO Survey없는 에러 띄우기
            return StartSurveyDto.builder().build();
        }

    }

    public SurveyDto getSurveyDetail(Long surveyId) {
        Optional<Survey> optionalSurvey = surveyRepository.findById(surveyId);
        //TODO request member 에 대해 처리해줘야함
        if (optionalSurvey.isPresent()) {
            Survey survey = optionalSurvey.get();

            return SurveyDto.fromEntity(survey);
        } else {
            return new SurveyDto();
        }
    }
    public SurveyStatisticsDto getSurveyStatistics(Long surveyId) {
        List<SurveyQuestion> surveyQuestions = surveyQuestionRepository.findBySurveyId(surveyId);
        List<SurveyQuestionStatDto> surveyQuestionStats = new ArrayList<>();

        for (SurveyQuestion surveyQuestion : surveyQuestions) {
            SurveyQuestionStatDto surveyQuestionStatDto = new SurveyQuestionStatDto();
            surveyQuestionStatDto.setOrder(surveyQuestion.getOrderNum());
            surveyQuestionStatDto.setQuestion(surveyQuestion.getQuestion());
            surveyQuestionStatDto.setIsMultipleChoice(surveyQuestion.getIsMultipleChoice());

            List<MultipleChoiceStatDto> multipleChoiceStatDtoList = new ArrayList<>();
            List<DescriptiveChoiceStatDto> descriptiveChoiceStatDtoList = new ArrayList<>();


            if (surveyQuestion.getIsMultipleChoice()) {
                Map<Integer, Long> resultMap = surveyQuestion.getMemberAnswerMultipleChoices().stream()
                        .collect(Collectors.groupingBy(MemberAnswerMultipleChoice::getOrderNum, Collectors.counting()));
                for (SurveyQuestionChoice surveyQuestionChoice : surveyQuestion.getSurveyQuestionChoices()) {
                    MultipleChoiceStatDto multipleChoiceStatDto = new MultipleChoiceStatDto();
                    multipleChoiceStatDto.setOrder(surveyQuestionChoice.getOrderNum());
                    multipleChoiceStatDto.setCount(resultMap.get(surveyQuestionChoice.getOrderNum()));
                    multipleChoiceStatDto.setDescription(surveyQuestionChoice.getChoiceDescription());
                    multipleChoiceStatDtoList.add(multipleChoiceStatDto);
                }
                surveyQuestionStatDto.setMultipleChoices(multipleChoiceStatDtoList);
            } else {
                for (MemberAnswerDescriptive memberAnswerDescriptive : surveyQuestion.getMemberAnswerDescriptives()) {
                    DescriptiveChoiceStatDto descriptiveChoiceStatDto = new DescriptiveChoiceStatDto();
                    descriptiveChoiceStatDto.setAnswer(memberAnswerDescriptive.getAnswer());
                    descriptiveChoiceStatDtoList.add(descriptiveChoiceStatDto);
                }
                surveyQuestionStatDto.setDescriptiveChoices(descriptiveChoiceStatDtoList);
            }

            surveyQuestionStats.add(surveyQuestionStatDto);
        }

        SurveyStatisticsDto surveyStatisticsDto = new SurveyStatisticsDto();
        surveyStatisticsDto.setSurveyQuestionStats(surveyQuestionStats);
        return surveyStatisticsDto;
    }
    public void saveSurveyStatistics(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId).get();


        SurveyStatisticsDto surveyStatisticsDto = getSurveyStatistics(surveyId);
        try {
            String surveyStatisticsJson = objectMapper.writeValueAsString(surveyStatisticsDto);
            SurveyStatistics surveyStatistics = new SurveyStatistics();
            surveyStatistics.setStatisticsJson(surveyStatisticsJson);
            surveyStatistics.setSurvey(survey);
            surveyStatisticsRepository.save(surveyStatistics);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void createSurveyAnswer(Long memberId, SurveyAnswersDto surveyAnswersDto) {
        //TODO user request Json에 대한 Validation을 해야함
        Optional<Survey> optionalSurvey = surveyRepository.findById(surveyAnswersDto.getSurveyId());
        Member findMember = memberRepository.findById(memberId).get();
        if (optionalSurvey.isPresent()) {
            Survey survey = optionalSurvey.get();
            survey.surveyParticipate();
            String UUID = UUIDGenerator.generateUUID();
            List<SurveyQuestion> sortedSurveyQuestionList = getSortedSurveyQuestionListFromSurvey(survey);
            List<SurveyAnswerDto> sortedSurveyAnswerDto = getSortedSurveyAnswerDtos(surveyAnswersDto);

            MemberSurvey memberSurvey=memberSurveyService.createMemberSurvey(memberId, survey, false);
            memberSurveyRepository.save(memberSurvey);
            findMember.setCouponCount(findMember.getCouponCount()+1);

            for (int index = 0; index < sortedSurveyQuestionList.size(); index++) {
                SurveyAnswerDto surveyAnswerDto = sortedSurveyAnswerDto.get(index);
                SurveyQuestion surveyQuestion = sortedSurveyQuestionList.get(index);
                if (surveyAnswerDto.getIsMultipleChoice()) {
                    MemberAnswerMultipleChoice memberAnswerMultipleChoice = memberAnswerServiceImpl.createMemberAnswerMultipleChoice(surveyAnswerDto, surveyQuestion, UUID);

                } else {
                    MemberAnswerDescriptive memberAnswerDescriptive = memberAnswerServiceImpl.createMemberAnswerDescriptive(surveyAnswerDto, surveyQuestion, UUID);
                }
            }
            if (survey.isFull()) {
                survey.setDone(Boolean.TRUE);
                publisher.sendId(survey.getId());
            }
        }
    }

    private static List<SurveyAnswerDto> getSortedSurveyAnswerDtos(SurveyAnswersDto surveyAnswersDto) {
        return surveyAnswersDto.getAnswers().stream()
                .sorted(Comparator.comparingInt(SurveyAnswerDto::getOrder))
                .collect(Collectors.toList());
    }

    private static List<SurveyQuestion> getSortedSurveyQuestionListFromSurvey(Survey survey) {
        return survey.getSurveyQuestions().stream()
                .sorted(Comparator.comparingInt(SurveyQuestion::getOrderNum))
                .collect(Collectors.toList());
    }

    public List<Survey> getRecommendSurveyList(Long memberId) {
        return surveyRepository.findSurveyByMemberJobAndAge(memberId);
    }

    public List<Survey> getStartSurveyList(){
        List<Survey> allSurveys = surveyRepository.findAll(); // 모든 Survey를 가져옴
        Collections.shuffle(allSurveys); // Survey 리스트를 랜덤하게 섞음
        return allSurveys.subList(0, Math.min(5, allSurveys.size())); // 5개의 Survey를 선택하여 반환
    }

    public List<Survey> getLoginSurveyList(Long memberId){
        List<Survey> allSurveys =surveyRepository.findSurveyByMemberJobAndAge(memberId); // 모든 Survey를 가져옴;
        if(allSurveys.isEmpty()){
            allSurveys=surveyRepository.findAll();
        }
        Collections.shuffle(allSurveys); // Survey 리스트를 랜덤하게 섞음
        return allSurveys.subList(0, Math.min(5, allSurveys.size())); // 5개의 Survey를 선택하여 반환
    }

    public List<Survey> getSearchSurveyList(String search) {
        return surveyRepository.findByTitleContaining(search);
    }
}
