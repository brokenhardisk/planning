package com.example.planning.service;

import com.example.planning.dto.WorkdayResponseDto;
import com.example.planning.exception.ItemNotFoundException;
import com.example.planning.persistence.model.Workday;
import com.example.planning.persistence.repository.WorkdayRepository;
import com.example.planning.service.impl.WorkdayAppServiceImpl;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class WorkdayAppServiceTest {
    @Mock
    private WorkdayRepository workdayRepository;

    private AppService workdayAppService;


    @BeforeEach
    public void setUp() {
        workdayAppService = new WorkdayAppServiceImpl();
        ReflectionTestUtils.setField(workdayAppService, "workdayRepository", workdayRepository);
    }

    @Test
    void testGetWorkdayList_OK() {
        List<Workday> expectedWorkdays = createWorkdayListSingleElement();
        Page<Workday> pagedResults = new PageImpl<>(expectedWorkdays);

        Mockito.when(workdayRepository.findAll(Mockito.isA(Pageable.class))).thenReturn(pagedResults);
        List<WorkdayResponseDto> actualWorkdays = workdayAppService.getItemList(0, 30);
        assertNotNull(actualWorkdays);
        assertEquals(expectedWorkdays.size(), actualWorkdays.size());
        validateGetWorkdays(expectedWorkdays.get(0), actualWorkdays.get(0));
    }

    @Test
    void testGetWorkdayById_OK() {

        Workday expectedWorkday = getWorkday();
        Mockito.when(workdayRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(expectedWorkday));
        WorkdayResponseDto actualWorkday = (WorkdayResponseDto) workdayAppService.getItemById(1);
        assertNotNull(actualWorkday);
        validateGetWorkdays(expectedWorkday, actualWorkday);
    }

    @Test
    void testGetWorkdayById_DOES_NOT_EXISTS() {

        Mockito.when(workdayRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            workdayAppService.getItemById(1);
        });
    }

    private void validateGetWorkdays(Workday expected, WorkdayResponseDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDate().toString(), actual.getDate());
    }

    private List<Workday> createWorkdayListSingleElement() {
        return List.of(getWorkday());
    }

    private Workday getWorkday() {
        return Workday.builder()
                .id(1)
                .date(LocalDate.now())
                .build();
    }
}
