package com.example.planning.controller;

import com.example.planning.dto.WorkdayResponseDto;
import com.example.planning.service.AppService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"api/planning/workday"})
public class WorkdayController {

    @Autowired
    @Qualifier("workdayAppService")
    private AppService workdayAppService;

    @GetMapping()
    @ApiOperation(value = "Gets a list of all workdays",
            response = WorkdayResponseDto.class,
            responseContainer = "List",
            produces = "application/json")
    public ResponseEntity<List<WorkdayResponseDto>> getWorkdayList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "limit", defaultValue = "30") int limit) {
        return ResponseEntity.ok(workdayAppService.getItemList(page, limit));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Gets workday by id",
            response = WorkdayResponseDto.class,
            produces = "application/json")
    public ResponseEntity<WorkdayResponseDto> getWorkdayById(@ApiParam(value = "Workday ID", required = true) @PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok((WorkdayResponseDto) workdayAppService.getItemById(id));
    }

}
