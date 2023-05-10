package com.ssafy.ssafvey.domain.survey.service;

import com.ssafy.ssafvey.domain.survey.dto.request.ChoiceDto;
import com.ssafy.ssafvey.domain.survey.dto.request.SurveyQuestionDto;
import com.ssafy.ssafvey.domain.survey.entity.Survey;
import com.ssafy.ssafvey.domain.survey.entity.SurveyQuestion;
import com.ssafy.ssafvey.domain.survey.entity.SurveyQuestionChoice;
import com.ssafy.ssafvey.domain.survey.repository.SurveyQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SurveyQuestionService {
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyQuestionChoiceService surveyQuestionChoiceService;

    @Autowired
    public SurveyQuestionService(SurveyQuestionRepository surveyQuestionRepository, SurveyQuestionChoiceService surveyQuestionChoiceService) {
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyQuestionChoiceService = surveyQuestionChoiceService;
    }

    public SurveyQuestion createSurveyQuestion(SurveyQuestionDto surveyQuestionDto, Survey survey) {

        SurveyQuestion surveyQuestion = SurveyQuestion.builder()
                .orderNum(surveyQuestionDto.getOrder())
                .question(surveyQuestionDto.getQuestion())
                .isMultipleChoice(surveyQuestionDto.getIsMultipleChoice())
                .build();
        surveyQuestion.setSurvey(survey);
        if (surveyQuestionDto.getIsMultipleChoice()) {
            List<SurveyQuestionChoice> surveyQuestionChoices = new ArrayList<>();

            for (ChoiceDto choiceDto : surveyQuestionDto.getChoices()) {
                SurveyQuestionChoice surveyQuestionChoice = surveyQuestionChoiceService
                        .createSurveyQuestionChoice(choiceDto, surveyQuestion);
                surveyQuestionChoices.add(surveyQuestionChoice);
            }

            surveyQuestion.setSurveyQuestionChoices(surveyQuestionChoices);
        }

        return surveyQuestionRepository.save(surveyQuestion);

    }

}
