package com.ssafy.ssafvey.domain.survey.service;

import com.ssafy.ssafvey.domain.member.entity.Job;
import com.ssafy.ssafvey.domain.survey.dto.request.SurveyDto;
import com.ssafy.ssafvey.domain.survey.dto.request.SurveyQuestionDto;
import com.ssafy.ssafvey.domain.survey.dto.request.TargetAgeDto;
import com.ssafy.ssafvey.domain.survey.entity.Survey;
import com.ssafy.ssafvey.domain.survey.entity.SurveyQuestion;
import com.ssafy.ssafvey.domain.survey.entity.SurveyTargetAge;
import com.ssafy.ssafvey.domain.survey.entity.SurveyTargetJob;
import com.ssafy.ssafvey.domain.survey.repository.JobRepository;
import com.ssafy.ssafvey.domain.survey.repository.SurveyRepository;
import com.ssafy.ssafvey.domain.survey.repository.SurveyTargetAgeRepository;
import com.ssafy.ssafvey.domain.survey.repository.SurveyTargetJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionService surveyQuestionService;
    private final SurveyTargetAgeRepository surveyTargetAgeRepository;
    private final SurveyTargetJobRepository surveyTargetJobRepository;

    private final JobRepository jobRepository;
    @Autowired
    public SurveyService(SurveyRepository surveyRepository, SurveyQuestionService surveyQuestionService, SurveyTargetAgeRepository surveyTargetAgeRepository, SurveyTargetJobRepository surveyTargetJobRepository, JobRepository jobRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyQuestionService = surveyQuestionService;
        this.surveyTargetAgeRepository = surveyTargetAgeRepository;
        this.surveyTargetJobRepository = surveyTargetJobRepository;
        this.jobRepository = jobRepository;
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
    public Survey createSurvey(SurveyDto surveyDto) {
        //TODO member_survey 도 만들어야함
        Survey survey = Survey.builder()
                .title(surveyDto.getTitle())
                .targetSurveyParticipants(surveyDto.getTargetSurveyParticipants())
                .targetGender(surveyDto.getTargetGender())
                .build();
        List<SurveyTargetJob> surveyTargetJobs = createSurveyTargetJobs(surveyDto.getTargetJob(), survey);
        List<SurveyTargetAge> surveyTargetAges = createSurveyTargetAgeList(surveyDto.getTargetAge(), survey);
        List<SurveyQuestion> surveyQuestions = new ArrayList<>();

        for (SurveyQuestionDto surveyQuestionDto : surveyDto.getSurveyQuestions()) {
            System.out.println(surveyQuestionDto);
            SurveyQuestion surveyQuestion = surveyQuestionService.createSurveyQuestion(surveyQuestionDto, survey);
            surveyQuestions.add(surveyQuestion);
        }

        survey.setSurveyTargetJobs(surveyTargetJobs);
        survey.setSurveyTargetAges(surveyTargetAges);
        survey.setSurveyQuestions(surveyQuestions);

        return surveyRepository.save(survey);
    }

}
