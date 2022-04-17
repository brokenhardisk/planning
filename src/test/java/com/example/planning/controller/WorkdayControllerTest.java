package com.example.planning.controller;

import com.example.planning.dto.WorkdayResponseDto;
import com.example.planning.persistence.model.Workday;
import com.example.planning.persistence.repository.WorkdayRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkdayControllerTest {
    private static final String INVALID_ID = "999";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkdayRepository workdayRepository;

    private Workday workday1;
    private static final String BASE_PATH = "/api/planning/workday";

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        workdayRepository.deleteAll();
        workday1 = workdayRepository.save(Workday.builder()
                .date(LocalDate.now())
                .build());
    }

    @Test
    public void test_getWorkdayList_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<WorkdayResponseDto> actualWorkdays = mapper.readValue(contentBody,
                new TypeReference<>() {
                });
        Assertions.assertNotNull(actualWorkdays);
        Assertions.assertEquals(1, actualWorkdays.size());
        validateWorkdays(workday1, actualWorkdays.get(0));
    }

    @Test
    public void test_getWorkdayById_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + workday1.getId()).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        WorkdayResponseDto workdayResponseDto = mapper.readValue(contentBody, WorkdayResponseDto.class);
        Assertions.assertNotNull(workdayResponseDto);
        validateWorkdays(workday1, workdayResponseDto);
    }

    @Test
    public void test_getWorkdayById_ERR_ITEM_NOT_FOUND() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + INVALID_ID).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private void validateWorkdays(Workday expected, WorkdayResponseDto actual) {
        assertEquals(expected.getDate().toString(), actual.getDate());
        assertEquals(expected.getId(), actual.getId());
    }
}
