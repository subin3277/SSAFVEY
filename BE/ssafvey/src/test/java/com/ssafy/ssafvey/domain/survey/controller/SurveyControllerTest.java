package com.ssafy.ssafvey.domain.survey.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SurveyController.class)
class SurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateSurvey() throws Exception {
        String json = "{\n" +
                "  \"title\": \"My Survey\",\n" +
                "  \"target_no_people\": 100,\n" +
                "  \"target_gender\": \"MALE\",\n" +
                "  \"target_job\":[1, 2],\n" +
                "  \"target_age\":[\n" +
                "    {\n" +
                "      \"min_age\": 18,\n" +
                "      \"max_age\": 30\n" +
                "    },\n" +
                "    {\n" +
                "      \"min_age\": 31,\n" +
                "      \"max_age\": 50\n" +
                "    }\n" +
                "  ],\n" +
                "  \"survey_questions\":[\n" +
                "    {\n" +
                "      \"order\": 1,\n" +
                "      \"question\": \"What is your favorite color?\",\n" +
                "      \"is_multiple_choice\":true,\n" +
                "      \"choices\":[\n" +
                "        {\n" +
                "          \"order\": 1,\n" +
                "          \"choice\": \"Red\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"order\": 2,\n" +
                "          \"choice\": \"Blue\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"order\": 2,\n" +
                "      \"question\": \"What is your favorite animal?\",\n" +
                "      \"is_multiple_choice\":false\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(post("/survey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
