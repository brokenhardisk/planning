package com.example.planning.service.impl;

import com.example.planning.dto.ShiftRequestDto;
import com.example.planning.dto.ShiftResponseDto;
import com.example.planning.exception.ApplicationException;
import com.example.planning.exception.ItemNotFoundException;
import com.example.planning.persistence.model.Shift;
import com.example.planning.persistence.model.Workday;
import com.example.planning.persistence.repository.ShiftRepository;
import com.example.planning.persistence.repository.UserRepository;
import com.example.planning.persistence.repository.WorkdayRepository;
import com.example.planning.service.ShiftAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("shiftAppService")
public class ShiftAppServiceImpl implements ShiftAppService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private WorkdayRepository workdayRepository;

    private static final String ITEM_NOT_FOUND = "Shift with given id does not exists: ";
    private static final String ITEM_ALREADY_EXISTS = "Shift already exists for the given date. Try updating the shift ";

    @Override
    public List<ShiftResponseDto> getItemList(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        List<ShiftResponseDto> resultList = new ArrayList<>();
        Optional.ofNullable(shiftRepository.findAll(pageableRequest))
                .ifPresent(res -> res.getContent()
                        .forEach(item -> resultList.add(mapToShiftResponseDto(item))));
        return resultList;
    }

    @Override
    public ShiftResponseDto addItem(ShiftRequestDto item) {
        Optional<Workday> response = workdayRepository.findByDate(LocalDate.parse(item.getDate()));
        Integer workdayId = 0;
        if (response.isEmpty()) {
            Workday newWorkday = workdayRepository.save(Workday.builder()
                    .date(LocalDate.parse(item.getDate()))
                    .build());
            workdayId = newWorkday.getId();
        } else {
            workdayId = response.get().getId();
            Optional<List<Shift>> shiftRes = shiftRepository.findByWorkdayId(response.get().getId());
            shiftRes.ifPresent(shifts -> shifts.forEach(shift -> {
                if (shift.getShiftStart().equals(LocalTime.parse(item.getStart())) ||
                        shift.getShiftEnd().equals(LocalTime.parse(item.getEnd()))) {
                    throw new ApplicationException(ITEM_ALREADY_EXISTS);
                }
            }));
        }
        Shift newShift = Shift.builder()
                .shiftStart(LocalTime.parse(item.getStart()))
                .shiftEnd(LocalTime.parse(item.getEnd()))
                .workdayId(workdayId)
                .build();
        Shift createdShift = shiftRepository.save(newShift);
        return mapToShiftResponseDto(createdShift);
    }

    @Override
    public ShiftResponseDto getItemById(Integer id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + id));
        return mapToShiftResponseDto(shift);
    }

    @Override
    public List<ShiftResponseDto> getShiftsByDate(String date) {
        Optional<Workday> response = workdayRepository.findByDate(LocalDate.parse(date));
        if (response.isEmpty()) {
            throw new ItemNotFoundException("Workday does not exist for given date " + date);
        }
        return getShiftsByWorkdayId(response.get().getId());

    }

    @Override
    public List<ShiftResponseDto> getShiftsByWorkdayId(Integer workdayId) {
        Optional<Workday> response = workdayRepository.findById(workdayId);
        if (response.isEmpty()) {
            throw new ItemNotFoundException("Workday does not exist for given ID " + workdayId);
        }
        List<ShiftResponseDto> resultsList = new ArrayList<>();
        shiftRepository.findByWorkdayId(workdayId)
                .ifPresent(shifts -> shifts.forEach(item -> {
                    resultsList.add(mapToShiftResponseDto(item));
                }));
        return resultsList;
    }

    @Override
    public ShiftResponseDto updateItem(ShiftRequestDto item, Integer id) {
        return null;
    }

    @Override
    @Transactional
    public void removeItem(Integer id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + id));
        userRepository.setStatusForShiftId(null, id);
        shiftRepository.delete(shift);
    }

    private ShiftResponseDto mapToShiftResponseDto(Shift shift) {
        return ShiftResponseDto.builder()
                .id(shift.getId())
                .workdayId(shift.getWorkdayId())
                .start(shift.getShiftStart().toString())
                .end(shift.getShiftEnd().toString())
                .build();
    }
}
