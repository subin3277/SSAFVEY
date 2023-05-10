package com.ssafy.ssafvey.domain.survey.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
public class SurveyQuestionChoice {
    @Id
    @GeneratedValue
    private Long id;

    private int orderNum;

    @Lob
    private String choiceDescription;

    private int count;

    @ManyToOne
    @JoinColumn(name = "survey_question_id")
    private SurveyQuestion surveyQuestion;

}
