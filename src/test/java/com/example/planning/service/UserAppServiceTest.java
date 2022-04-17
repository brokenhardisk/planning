package com.example.planning.service;

import com.example.planning.dto.UserRequestDto;
import com.example.planning.dto.UserResponseDto;
import com.example.planning.exception.ApplicationException;
import com.example.planning.exception.ItemNotFoundException;
import com.example.planning.persistence.model.User;
import com.example.planning.persistence.repository.UserRepository;
import com.example.planning.service.impl.UserAppServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class UserAppServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserAppService userAppService;


    @BeforeEach
    public void setUp() {
        userAppService = new UserAppServiceImpl();
        ReflectionTestUtils.setField(userAppService, "userRepository", userRepository);
    }

    @Test
    void testGetUserList_OK() {
        List<User> expectedUsers = createUserListSingleElement();
        Page<User> pagedResults = new PageImpl<>(expectedUsers);

        Mockito.when(userRepository.findAll(Mockito.isA(Pageable.class))).thenReturn(pagedResults);
        List<UserResponseDto> actualUsers = userAppService.getItemList(0, 30);
        assertNotNull(actualUsers);
        assertEquals(expectedUsers.size(), actualUsers.size());
        validateGetUsers(expectedUsers.get(0), actualUsers.get(0));
    }

    @Test
    void testGetUserList_WORKING_OK() {
        List<User> expectedUsers = createUserListSingleElement();
        Mockito.when(userRepository.findByShiftIdIsNotNull()).thenReturn(Optional.of(expectedUsers));
        List<UserResponseDto> actualUsers = userAppService.getAllWorkingUsers();
        assertNotNull(actualUsers);
        assertEquals(expectedUsers.size(), actualUsers.size());
        validateGetUsers(expectedUsers.get(0), actualUsers.get(0));
    }

    @Test
    void testGetUserList_IDLE_OK() {
        List<User> expectedUsers = createUserListSingleElementIdle();
        Mockito.when(userRepository.findByShiftIdIsNull()).thenReturn(Optional.of(expectedUsers));
        List<UserResponseDto> actualUsers = userAppService.getAllNonWorkingUsers();
        assertNotNull(actualUsers);
        assertEquals(expectedUsers.size(), actualUsers.size());
        validateGetUsers(expectedUsers.get(0), actualUsers.get(0));
    }

    @Test
    void testGetUserById_OK() {

        User expectedUser = getUser();
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(expectedUser));
        UserResponseDto actualUser = userAppService.getItemById(1);
        assertNotNull(actualUser);
        validateGetUsers(expectedUser, actualUser);
    }

    @Test
    void testGetUserById_DOES_NOT_EXISTS() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            userAppService.getItemById(1);
        });
    }

    @Test
    void testRemoveUserById_DOES_NOT_EXISTS() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            userAppService.removeItem(1);
        });
    }

    @Test
    void testRemoveUserById_OK() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new User()));
        Mockito.doNothing().when(userRepository).delete(Mockito.any(User.class));
        userAppService.removeItem(1);
    }

    @Test
    void testUpdateUserById_DOES_NOT_EXISTS() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            userAppService.updateItem(UserRequestDto.builder().build(), 1);
        });
    }

    @Test
    void testUpdateUserById_ok() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new User()));
        User expectedUser = getUser();
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("John Doe")
                .shiftId(1)
                .build();
        UserResponseDto actualUser = userAppService.updateItem(UserRequestDto.builder().build(), 1);
        assertNotNull(actualUser.getId());
        validateAddUpdateUser(userRequestDto, actualUser);
    }

    @Test
    void testAddUser_NAME_ALREADY_EXISTS() {

        Mockito.when(userRepository.findByName(Mockito.anyString()))
                .thenReturn(Optional.of(new User()));

        Assertions.assertThrows(ApplicationException.class, () -> {
            userAppService.addItem(UserRequestDto.builder().name("JD").build());
        });
    }

    @Test
    void testAddUser_OK() {
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        User expectedUser = getUser();
        expectedUser.setShiftId(null);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .name("John Doe")
                .build();

        UserResponseDto actualUser = userAppService.addItem(userRequestDto);

        assertNotNull(actualUser.getId());
        validateAddUpdateUser(userRequestDto, actualUser);
    }

    @Test
    void testAddUser_OK_withoutShift() {
        Mockito.when(userRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        User expectedUser = getUserWithoutShift();
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);
        UserRequestDto UserRequestDto = new UserRequestDto();
        UserRequestDto.setName("ABCS");

        UserResponseDto actualUser = userAppService.addItem(UserRequestDto);

        assertNotNull(actualUser.getId());
        validateAddUpdateUser(UserRequestDto, actualUser);
    }


    private void validateGetUsers(User expected, UserResponseDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getShiftId(), actual.getShiftId());

    }

    private void validateAddUpdateUser(UserRequestDto expected, UserResponseDto actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getShiftId(), actual.getShiftId());
    }

    private List<User> createUserListSingleElement() {
        return List.of(getUser());
    }

    private User getUser() {
        return User.builder()
                .id(1)
                .name("John Doe")
                .shiftId(1)
                .build();
    }

    private List<User> createUserListSingleElementIdle() {
        return List.of(getUserWithoutShift());
    }

    private User getUserWithoutShift() {
        return User.builder()
                .id(1)
                .name("ABCS")
                .build();
    }
}
