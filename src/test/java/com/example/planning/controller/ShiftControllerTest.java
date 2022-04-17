package com.example.planning.controller;

import com.example.planning.dto.ShiftRequestDto;
import com.example.planning.dto.ShiftResponseDto;
import com.example.planning.persistence.model.Shift;
import com.example.planning.persistence.model.Workday;
import com.example.planning.persistence.repository.ShiftRepository;
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
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class ShiftControllerTest {
    private static final String INVALID_ID = "999";
    private static final Integer INVALID_SHIFT_ID = 3;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private WorkdayRepository workdayRepository;

    private Shift shift1;
    private Shift shift2;
    private Workday newWorkday;
    private static final String BASE_PATH = "/api/planning/shift";
    private static final String BY_WORKDAY_ID = "/workdayById/";
    private static final String BY_WORKDAY_DATE = "/workdayByDate/";

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        workdayRepository.deleteAll();
        newWorkday = workdayRepository.save(Workday.builder()
                .date(LocalDate.now())
                .build());
        shiftRepository.deleteAll();
        shift1 = shiftRepository.save(createShift(LocalTime.parse("08:00"), LocalTime.parse("16:00"), newWorkday.getId()));

    }

    @Test
    public void test_getShiftList_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<ShiftResponseDto> actualShifts = mapper.readValue(contentBody,
                new TypeReference<>() {
                });
        Assertions.assertNotNull(actualShifts);
        Assertions.assertEquals(1, actualShifts.size());
        validateShifts(shift1, actualShifts.get(0));
    }

    @Test
    public void test_getShiftList_BY_WORKDAY_ID_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH + BY_WORKDAY_ID + newWorkday.getId()).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<ShiftResponseDto> actualShifts = mapper.readValue(contentBody,
                new TypeReference<>() {
                });
        Assertions.assertNotNull(actualShifts);
        Assertions.assertEquals(1, actualShifts.size());
        validateShifts(shift1, actualShifts.get(0));
    }

    @Test
    public void test_getShiftList_BY_WORKDAY_DATE_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH + BY_WORKDAY_DATE + newWorkday.getDate().toString()).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<ShiftResponseDto> actualShifts = mapper.readValue(contentBody,
                new TypeReference<>() {
                });
        Assertions.assertNotNull(actualShifts);
        Assertions.assertEquals(1, actualShifts.size());
        validateShifts(shift1, actualShifts.get(0));
    }

    @Test
    public void test_getShiftById_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + shift1.getId()).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        ShiftResponseDto shiftResponseDto = mapper.readValue(contentBody, ShiftResponseDto.class);
        Assertions.assertNotNull(shiftResponseDto);
        validateShifts(shift1, shiftResponseDto);
    }

    @Test
    public void test_getShiftById_ERR_ITEM_NOT_FOUND() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + INVALID_ID).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void test_deleteShiftById_OK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + shift1.getId())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void test_deleteShiftById_ERR_ITEM_NOT_FOUND() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + INVALID_ID).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void test_createShift_OK() throws Exception {
        shift2 = getShift("00:00", "08:00");
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createShiftRequestDto("00:00", "08:00", "2022-12-22"))))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
        ShiftResponseDto ShiftResponseDto = mapper.readValue(contentBody, ShiftResponseDto.class);
        Assertions.assertNotNull(ShiftResponseDto);
        validateShifts(shift2, ShiftResponseDto);
    }

    @Test
    void test_createShift_ERR_Shift_ALREADY_EXISTS() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createShiftRequestDto("08:00", "16:00", newWorkday.getDate().toString()))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private Shift getShift(String shiftStart, String shiftEnd) {
        return Shift.builder()
                .shiftStart(LocalTime.parse(shiftStart))
                .shiftEnd(LocalTime.parse(shiftEnd))
                .build();
    }

    private void validateShifts(Shift expected, ShiftResponseDto actual) {
        assertEquals(expected.getShiftStart().toString(), actual.getStart());
        assertEquals(expected.getShiftEnd().toString(), actual.getEnd());
    }

    private ShiftRequestDto createShiftRequestDto(String start, String end, String date) {
        return ShiftRequestDto.builder()
                .start(start)
                .end(end)
                .date(date)
                .build();
    }

    private Shift createShift(LocalTime start, LocalTime end, Integer workdayId) {
        return Shift.builder()
                .shiftStart(start)
                .shiftEnd(end)
                .workdayId(workdayId)
                .build();
    }

}
