package com.ssafy.ssafvey.domain.survey.repository;

import com.ssafy.ssafvey.domain.survey.entity.SurveyQuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyQuestionChoiceRepository extends JpaRepository<SurveyQuestionChoice, Long> {

}
