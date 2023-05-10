package com.ssafy.ssafvey.domain.survey.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.ssafvey.domain.member.entity.GenderType;
import com.ssafy.ssafvey.domain.member.entity.MemberSurvey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Survey {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private LocalDateTime createDate;

    private LocalDateTime endDate;

    private int targetSurveyParticipants;

    private GenderType targetGender;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int surveyParticipants;

    private String description;

    private String organization;

    private boolean isDone;


    @OneToMany(mappedBy = "survey",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SurveyQuestion> surveyQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "survey",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SurveyTargetAge> surveyTargetAges = new ArrayList<>();


    @OneToMany(mappedBy = "survey",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SurveyTargetJob> surveyTargetJobs = new ArrayList<>();

    public void setSurveyQuestions(List<SurveyQuestion> surveyQuestions) {
        this.surveyQuestions = surveyQuestions;
    }

    public void setSurveyTargetAges(List<SurveyTargetAge> surveyTargetAges) {
        this.surveyTargetAges = surveyTargetAges;
    }

    public void setSurveyTargetJobs(List<SurveyTargetJob> surveyTargetJobs) {
        this.surveyTargetJobs = surveyTargetJobs;
    }

    @OneToMany(mappedBy = "survey",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MemberSurvey> memberSurveys = new ArrayList<>();


    @PrePersist
    public void setCreatedAt() {
        this.createDate = LocalDateTime.now();
    }

}
