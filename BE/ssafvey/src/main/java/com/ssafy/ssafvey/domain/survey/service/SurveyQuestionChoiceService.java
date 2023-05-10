package com.ssafy.ssafvey.domain.survey.service;

import com.ssafy.ssafvey.domain.survey.dto.request.ChoiceDto;
import com.ssafy.ssafvey.domain.survey.entity.SurveyQuestion;
import com.ssafy.ssafvey.domain.survey.entity.SurveyQuestionChoice;
import com.ssafy.ssafvey.domain.survey.repository.SurveyQuestionChoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyQuestionChoiceService {

    private final SurveyQuestionChoiceRepository surveyQuestionChoiceRepository;

    @Autowired
    public SurveyQuestionChoiceService(SurveyQuestionChoiceRepository surveyQuestionChoiceRepository) {
        this.surveyQuestionChoiceRepository = surveyQuestionChoiceRepository;
    }

    public SurveyQuestionChoice createSurveyQuestionChoice(ChoiceDto choiceDto, SurveyQuestion surveyQuestion) {
        SurveyQuestionChoice surveyQuestionChoice = SurveyQuestionChoice.builder()
                .orderNum(choiceDto.getOrder())
                .choiceDescription(choiceDto.getChoice())
                .surveyQuestion(surveyQuestion)
                .build();

        return surveyQuestionChoiceRepository.save(surveyQuestionChoice);


    }
}
