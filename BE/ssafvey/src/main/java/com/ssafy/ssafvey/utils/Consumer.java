package com.ssafy.ssafvey.utils;

import com.ssafy.ssafvey.domain.survey.dto.MQSendSurveyDto;
import com.ssafy.ssafvey.domain.survey.dto.SurveyStatisticsDto;
import com.ssafy.ssafvey.domain.survey.service.SurveyService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Consumer {
    private final SurveyService surveyService;

    public Consumer(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    // hello 큐의 메시지가 컨슘되는지 확인하기위해 로그 추가
    @RabbitListener(queues = "hello")
    public void consume(Message message){
        System.out.println(message);
    }

    @RabbitListener(queues = "my-queue")
    public void consumeMyQueue(final MQSendSurveyDto mqSendSurveyDto){
        surveyService.saveSurveyStatistics(mqSendSurveyDto.getId());
    }
}