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
public class SurveyTargetAge {
    @Id
    @GeneratedValue
    private Long id;

    private int minAge;

    private int maxAge;

    @ManyToOne
    private Survey survey;

}
