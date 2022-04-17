package com.example.planning.controller;

import com.example.planning.dto.UserRequestDto;
import com.example.planning.dto.UserResponseDto;
import com.example.planning.exception.ErrorMessageDto;
import com.example.planning.persistence.model.Shift;
import com.example.planning.persistence.model.User;
import com.example.planning.persistence.model.Workday;
import com.example.planning.persistence.repository.ShiftRepository;
import com.example.planning.persistence.repository.UserRepository;
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
public class UserControllerTest {
    private static final String INVALID_ID = "999";
    private static final Integer INVALID_SHIFT_ID = 3;
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private WorkdayRepository workdayRepository;

    private User user1;
    private User user2;
    private Shift shift1;
    private static final String BASE_PATH = "/api/planning/user";
    private static final String WORKING = "/working";
    private static final String IDLE = "/idle";

    private final com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        workdayRepository.deleteAll();
        Workday newWorkday = workdayRepository.save(Workday.builder()
                .date(LocalDate.now())
                .build());
        userRepository.deleteAll();
        user1 = userRepository.save(getUser("John Doe", null));
        shiftRepository.deleteAll();
        shift1 = shiftRepository.save(createShift(LocalTime.parse("08:00"), LocalTime.parse("16:00"),newWorkday.getId()));

    }

    @Test
    public void test_getUserList_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<UserResponseDto> actualUsers = mapper.readValue(contentBody,
                new TypeReference<>() {
                });
        Assertions.assertNotNull(actualUsers);
        Assertions.assertEquals(1, actualUsers.size());
        validateUsers(user1, actualUsers.get(0));
    }

    @Test
    public void test_getUserList_WORKING_USERS_OK() throws Exception {
        user2 = userRepository.save(getUser("J Doe", shift1.getId()));
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH+WORKING).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<UserResponseDto> actualUsers = mapper.readValue(contentBody,
                new TypeReference<>() {
                });
        Assertions.assertNotNull(actualUsers);
        Assertions.assertEquals(1, actualUsers.size());
        validateUsers(user2, actualUsers.get(0));
    }

    @Test
    public void test_getUserList_IDLE_USERS_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH+IDLE).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<UserResponseDto> actualUsers = mapper.readValue(contentBody,
                new TypeReference<>() {
                });
        Assertions.assertNotNull(actualUsers);
        Assertions.assertEquals(1, actualUsers.size());
        validateUsers(user1, actualUsers.get(0));
    }
    @Test
    public void test_getUserById_OK() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + user1.getId()).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        UserResponseDto userResponseDto = mapper.readValue(contentBody, UserResponseDto.class);
        Assertions.assertNotNull(userResponseDto);
        validateUsers(user1, userResponseDto);
    }

    @Test
    public void test_getUserById_ERR_ITEM_NOT_FOUND() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + INVALID_ID).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void test_deleteUserById_OK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + user1.getId())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void test_deleteUserById_ERR_ITEM_NOT_FOUND() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + INVALID_ID).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void test_createUser_BAD_REQUEST_NAME_NULL() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserRequestDto(null, shift1.getId()))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse()
                .getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<ErrorMessageDto> errors = mapper.readValue(contentBody, new TypeReference<>() {
        });
        Assertions.assertNotNull(errors);
        Assertions.assertNotNull(errors.get(0).getField());
        Assertions.assertEquals("name", errors.get(0).getField());
    }

    @Test
    void test_createUser_BAD_REQUEST_INVALID_SHIFT() throws Exception {
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserRequestDto("Doe Doe", INVALID_SHIFT_ID))))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResponse()
                .getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
    }

    @Test
    void test_createUser_OK() throws Exception {
        user2 = getUser("Joan Doe", shift1.getId());
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserRequestDto("Joan Doe", shift1.getId()))))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
        UserResponseDto userResponseDto = mapper.readValue(contentBody, UserResponseDto.class);
        Assertions.assertNotNull(userResponseDto);
        validateUsers(user2, userResponseDto);
    }

    @Test
    void test_createUser_without_Shift_OK() throws Exception {
        user2 = getUser("Jane", null);
        UserRequestDto reqObj = new UserRequestDto();
        reqObj.setName("Jane");
        String contentBody = mockMvc
                .perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reqObj)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
        UserResponseDto UserResponseDto = mapper.readValue(contentBody, UserResponseDto.class);
        Assertions.assertNotNull(UserResponseDto);
        validateUsers(user2, UserResponseDto);
    }

    @Test
    void test_createUser_ERR_User_ALREADY_EXISTS() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createUserRequestDto("John Doe", null))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void test_updateUser_OK() throws Exception {
        user2 = getUser("John Doe", shift1.getId());
        String contentBody = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/" + user1.getId()).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createUserRequestDto("John Doe", shift1.getId()))))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        UserResponseDto UserResponseDto = mapper.readValue(contentBody, UserResponseDto.class);
        Assertions.assertNotNull(UserResponseDto);
        validateUsers(user2, UserResponseDto);
    }

    @Test
    void test_updateUser_BAD_REQUEST_NAME_NULL() throws Exception {
        String contentBody = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/" + user1.getId()).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createUserRequestDto(null, 1))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse()
                .getContentAsString();
        Assertions.assertTrue(StringUtils.hasLength(contentBody));
        List<ErrorMessageDto> errors = mapper.readValue(contentBody, new TypeReference<>() {
        });
        Assertions.assertNotNull(errors);
        Assertions.assertNotNull(errors.get(0).getField());
        Assertions.assertEquals("name", errors.get(0).getField());
    }

    @Test
    void test_updateUser_SHIFT_NOT_FOUND() throws Exception {
        String contentBody = mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/" + user1.getId()).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createUserRequestDto("PLM", 3))))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResponse()
                .getContentAsString();
    }

    @Test
    void test_updateUser_ERR_User_DOES_NOT_EXISTS() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/" + user1.getId()).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createUserRequestDto(null, 3))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void test_updateUser_ERR_User_ALREADY_EXISTS() throws Exception {
        user2 = userRepository.save(getUser("Jane Sky", 1));
        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/" + user1.getId()).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createUserRequestDto("Jane Sky", 1))))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private User getUser(String name, Integer shiftId) {
        return User.builder()
                .name(name)
                .shiftId(shiftId)
                .build();
    }

    private void validateUsers(User expected, UserResponseDto actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getShiftId(), actual.getShiftId());
    }

    private UserRequestDto createUserRequestDto(String name, Integer shiftId) {
        return UserRequestDto.builder()
                .name(name)
                .shiftId(shiftId)
                .build();
    }

    private Shift createShift(LocalTime start, LocalTime end,Integer workdayId) {
        return Shift.builder()
                .shiftStart(start)
                .shiftEnd(end)
                .workdayId(workdayId)
                .build();
    }

}
