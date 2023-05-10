package com.ssafy.ssafvey.domain.survey.controller;

import com.ssafy.ssafvey.domain.survey.dto.StartSurveyDto;
import com.ssafy.ssafvey.domain.survey.dto.request.SurveyDto;
import com.ssafy.ssafvey.domain.survey.dto.request.SurveyQuestionDto;
import com.ssafy.ssafvey.domain.survey.entity.Survey;
import com.ssafy.ssafvey.domain.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;


    @PostMapping("/survey")
    public ResponseEntity<?> createSurvey(HttpServletRequest request, @RequestBody SurveyDto surveyDto) {
        Survey survey = surveyService.createSurvey(surveyDto);
        return new ResponseEntity<>(survey, HttpStatus.CREATED);
    }
    @GetMapping("/survey/start/{survey_id}")
    public ResponseEntity<?> startSurvey(HttpServletRequest request, @PathVariable Long survey_id) {
        StartSurveyDto startSurveyDto = surveyService.getStartSurveyById(survey_id);
        return new ResponseEntity<>(startSurveyDto, HttpStatus.ACCEPTED);
    }
}
