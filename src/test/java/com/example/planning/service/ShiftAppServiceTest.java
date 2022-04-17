package com.example.planning.service;

import com.example.planning.dto.ShiftRequestDto;
import com.example.planning.dto.ShiftResponseDto;
import com.example.planning.exception.ApplicationException;
import com.example.planning.exception.ItemNotFoundException;
import com.example.planning.persistence.model.Shift;
import com.example.planning.persistence.model.Workday;
import com.example.planning.persistence.repository.ShiftRepository;
import com.example.planning.persistence.repository.UserRepository;
import com.example.planning.persistence.repository.WorkdayRepository;
import com.example.planning.service.impl.ShiftAppServiceImpl;
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

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class ShiftAppServiceTest {
    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private WorkdayRepository workdayRepository;

    @Mock
    private UserRepository userRepository;

    private ShiftAppService shiftAppService;


    @BeforeEach
    public void setUp() {
        shiftAppService = new ShiftAppServiceImpl();
        ReflectionTestUtils.setField(shiftAppService, "shiftRepository", shiftRepository);
        ReflectionTestUtils.setField(shiftAppService, "workdayRepository", workdayRepository);
        ReflectionTestUtils.setField(shiftAppService, "userRepository", userRepository);
    }

    @Test
    void testGetShiftList_OK() {
        List<Shift> expectedShifts = createShiftListSingleElement();
        Page<Shift> pagedResults = new PageImpl<>(expectedShifts);

        Mockito.when(shiftRepository.findAll(Mockito.isA(Pageable.class))).thenReturn(pagedResults);
        List<ShiftResponseDto> actualShifts = shiftAppService.getItemList(0, 30);
        assertNotNull(actualShifts);
        assertEquals(expectedShifts.size(), actualShifts.size());
        validateGetShifts(expectedShifts.get(0), actualShifts.get(0));
    }

    @Test
    void testGetShiftList_BY_WORKDAY_ID_OK() {
        List<Shift> expectedShifts = createShiftListSingleElement();
        Mockito.when(workdayRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(Workday.builder().build()));
        Mockito.when(shiftRepository.findByWorkdayId(Mockito.anyInt())).thenReturn(Optional.of(expectedShifts));
        List<ShiftResponseDto> actualShifts = shiftAppService.getShiftsByWorkdayId(1);
        assertNotNull(actualShifts);
        assertEquals(expectedShifts.size(), actualShifts.size());
        validateGetShifts(expectedShifts.get(0), actualShifts.get(0));
    }

    @Test
    void testGetShiftList_BY_WORKDAY_ID_DOES_NOT_EXISTS() {
        Mockito.when(workdayRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            shiftAppService.getShiftsByWorkdayId(1);
        });
    }
    @Test
    void testGetShiftList_BY_WORKDAY_DATE_OK() {
        List<Shift> expectedShifts = createShiftListSingleElement();
        Mockito.when(workdayRepository.findByDate(Mockito.any())).thenReturn(Optional.of(Workday.builder().id(1).build()));
        Mockito.when(workdayRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(Workday.builder().build()));
        Mockito.when(shiftRepository.findByWorkdayId(Mockito.anyInt())).thenReturn(Optional.of(expectedShifts));
        List<ShiftResponseDto> actualShifts = shiftAppService.getShiftsByDate("2022-12-22");
        assertNotNull(actualShifts);
        assertEquals(expectedShifts.size(), actualShifts.size());
        validateGetShifts(expectedShifts.get(0), actualShifts.get(0));
    }

    @Test
    void testGetShiftList_BY_WORKDAY_DATE_DOES_NOT_EXISTS() {
        Mockito.when(workdayRepository.findByDate(Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            shiftAppService.getShiftsByDate("2022-12-22");
        });
    }

    @Test
    void testGetShiftById_OK() {

        Shift expectedShift = getShift();
        Mockito.when(shiftRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(expectedShift));
        ShiftResponseDto actualShift = shiftAppService.getItemById(1);
        assertNotNull(actualShift);
        validateGetShifts(expectedShift, actualShift);
    }

    @Test
    void testGetShiftById_DOES_NOT_EXISTS() {

        Mockito.when(shiftRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            shiftAppService.getItemById(1);
        });
    }

    @Test
    void testRemoveShiftById_DOES_NOT_EXISTS() {

        Mockito.when(shiftRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            shiftAppService.removeItem(1);
        });
    }

    @Test
    void testRemoveShiftById_OK() {

        Mockito.when(shiftRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new Shift()));
        Mockito.doNothing().when(shiftRepository).delete(Mockito.any(Shift.class));
        shiftAppService.removeItem(1);
    }

    @Test
    void testAddShift_ALREADY_EXISTS() {
        Mockito.when(workdayRepository.findByDate(Mockito.any()))
                .thenReturn(Optional.of(Workday.builder().id(1).build()));
        Mockito.when(shiftRepository.findByWorkdayId(Mockito.anyInt()))
                .thenReturn(Optional.of(createShiftListSingleElement()));

        Assertions.assertThrows(ApplicationException.class, () -> {
            shiftAppService.addItem(ShiftRequestDto.builder()
                    .start("08:00")
                    .end("16:00")
                    .date("2022-12-22")
                    .build());
        });
    }

    @Test
    void testAddShift_OK() {
        Mockito.when(workdayRepository.findByDate(Mockito.any()))
                .thenReturn(Optional.of(Workday.builder().id(1).build()));
        Mockito.when(shiftRepository.findByWorkdayId(Mockito.anyInt()))
                .thenReturn(Optional.of(Collections.emptyList()));
        Shift expectedShift = getShift();
        Mockito.when(shiftRepository.save(Mockito.any(Shift.class))).thenReturn(expectedShift);
        ShiftRequestDto shiftRequestDto = ShiftRequestDto.builder()
                .start("08:00")
                .end("16:00")
                .date("2022-12-22")
                .build();

        ShiftResponseDto actualShift = shiftAppService.addItem(shiftRequestDto);

        assertNotNull(actualShift.getId());
        validateAddUpdateShift(shiftRequestDto, actualShift);
    }


    private void validateGetShifts(Shift expected, ShiftResponseDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getShiftStart().toString(), actual.getStart());
        assertEquals(expected.getShiftEnd().toString(), actual.getEnd());

    }

    private void validateAddUpdateShift(ShiftRequestDto expected, ShiftResponseDto actual) {
        assertEquals(expected.getStart(), actual.getStart());
        assertEquals(expected.getEnd(), actual.getEnd());
    }

    private List<Shift> createShiftListSingleElement() {
        return List.of(getShift());
    }

    private Shift getShift() {
        return Shift.builder()
                .id(1)
                .shiftStart(LocalTime.parse("08:00"))
                .shiftEnd(LocalTime.parse("16:00"))
                .workdayId(1)
                .build();
    }
}
