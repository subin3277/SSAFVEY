package com.ssafy.ssafvey.domain.survey.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SurveyQuestionDto {
    private int order;
    private String question;

    private Boolean isMultipleChoice;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ChoiceDto> choices;

}
