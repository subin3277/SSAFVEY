package com.ssafy.ssafvey.domain.survey.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ssafy.ssafvey.domain.member.entity.GenderType;
import com.ssafy.ssafvey.utils.json.deserializer.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@ToString
public class SurveyDto {
    private String title;
    private int targetSurveyParticipants;
    private GenderType targetGender;
    private List<Long> targetJob;
    private List<TargetAgeDto> targetAge;
    private List<SurveyQuestionDto> surveyQuestions;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDate;
    private String description;
    private String organization;
}