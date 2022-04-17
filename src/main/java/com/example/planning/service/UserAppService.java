package com.example.planning.service;


import com.example.planning.dto.UserRequestDto;
import com.example.planning.dto.UserResponseDto;

import java.util.List;

public interface UserAppService extends AppService<UserRequestDto, UserResponseDto> {
    List<UserResponseDto> getAllWorkingUsers();

    List<UserResponseDto> getAllNonWorkingUsers();
}
