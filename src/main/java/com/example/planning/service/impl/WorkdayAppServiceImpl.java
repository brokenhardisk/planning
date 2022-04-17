package com.example.planning.service.impl;

import com.example.planning.dto.WorkdayResponseDto;
import com.example.planning.exception.ItemNotFoundException;
import com.example.planning.persistence.model.Workday;
import com.example.planning.persistence.repository.WorkdayRepository;
import com.example.planning.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("workdayAppService")
public class WorkdayAppServiceImpl  implements AppService<String, WorkdayResponseDto> {

    @Autowired
    private WorkdayRepository workdayRepository;

    private static final String ITEM_NOT_FOUND = "Workday with given id does not exists: ";

    @Override
    public List<WorkdayResponseDto> getItemList(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        List<WorkdayResponseDto> resultList = new ArrayList<>();
        Optional.ofNullable(workdayRepository.findAll(pageableRequest))
                .ifPresent(res -> res.getContent()
                        .forEach(item -> resultList.add(mapToWorkdayResponseDto(item))));
        return resultList;
    }

    @Override
    public WorkdayResponseDto addItem(String item) {
        return null;
    }

    @Override
    public WorkdayResponseDto getItemById(Integer id) {
        Workday workday = workdayRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + id));
        return mapToWorkdayResponseDto(workday);
    }

    @Override
    public WorkdayResponseDto updateItem(String item, Integer id) {
        return null;
    }

    @Override
    public void removeItem(Integer id) {

    }

    private WorkdayResponseDto mapToWorkdayResponseDto(Workday workday) {
        return WorkdayResponseDto.builder()
                .id(workday.getId())
                .date(workday.getDate().toString())
                .build();
    }
}
