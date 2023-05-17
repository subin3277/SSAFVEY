package com.ssafy.ssafvey.utils;

import com.ssafy.ssafvey.domain.survey.dto.MQSendSurveyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Publisher {
    private final RabbitTemplate rabbitTemplate;

    public void send(Object message) {
        rabbitTemplate.convertAndSend("my",message);
    }

    public void sendId(Long id) {
        MQSendSurveyDto mqSendSurveyDto = new MQSendSurveyDto();
        mqSendSurveyDto.setId(id);

        rabbitTemplate.convertAndSend("my-queue", mqSendSurveyDto);
    }

}
