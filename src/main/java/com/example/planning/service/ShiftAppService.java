package com.example.planning.service;


import com.example.planning.dto.ShiftRequestDto;
import com.example.planning.dto.ShiftResponseDto;

import java.util.List;

public interface ShiftAppService extends AppService<ShiftRequestDto, ShiftResponseDto> {
    List<ShiftResponseDto> getShiftsByDate(String date);

    List<ShiftResponseDto> getShiftsByWorkdayId(Integer workdayId);
}
