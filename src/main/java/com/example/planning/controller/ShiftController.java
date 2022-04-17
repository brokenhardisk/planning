package com.example.planning.controller;

import com.example.planning.dto.ShiftRequestDto;
import com.example.planning.dto.ShiftResponseDto;
import com.example.planning.service.ShiftAppService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = {"api/planning/shift"})
public class ShiftController {

    @Autowired
    @Qualifier("shiftAppService")
    private ShiftAppService shiftAppService;

    @GetMapping()
    @ApiOperation(value = "Gets a list of all shifts",
            response = ShiftResponseDto.class,
            responseContainer = "List",
            produces = "application/json")
    public ResponseEntity<List<ShiftResponseDto>> getShiftList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "limit", defaultValue = "30") int limit) {
        return ResponseEntity.ok(shiftAppService.getItemList(page, limit));
    }

    @PostMapping()
    @ApiOperation(value = "Adds a shift to a date",
            consumes = "application/json",
            response = ShiftResponseDto.class,
            produces = "application/json")
    public ResponseEntity<ShiftResponseDto> addShift(@ApiParam(value = "Shift data") @Valid @RequestBody ShiftRequestDto item) {
        return new ResponseEntity<>((ShiftResponseDto) shiftAppService.addItem(item), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Gets shift by id",
            response = ShiftResponseDto.class,
            produces = "application/json")
    public ResponseEntity<ShiftResponseDto> getShiftById(@ApiParam(value = "Shift ID", required = true) @PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok((ShiftResponseDto) shiftAppService.getItemById(id));
    }

    @GetMapping("/workdayById/{id}")
    @ApiOperation(value = "Gets shift by Workday id",
            response = ShiftResponseDto.class,
            produces = "application/json")
    public ResponseEntity<List<ShiftResponseDto>> getShiftByWorkdayId(@ApiParam(value = "Workday ID", required = true) @PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(shiftAppService.getShiftsByWorkdayId(id));
    }

    @GetMapping("/workdayByDate/{date}")
    @ApiOperation(value = "Gets shift by date",
            response = ShiftResponseDto.class,
            produces = "application/json")
    public ResponseEntity<List<ShiftResponseDto>> getShiftByDate(@ApiParam(value = "Workday Date", required = true) @PathVariable(value = "date") String date) {
        return ResponseEntity.ok(shiftAppService.getShiftsByDate(date));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Deletes a shift")
    public ResponseEntity<Void> removeShift(@ApiParam(value = "Shift ID", required = true) @PathVariable(value = "id") Integer id) {
        shiftAppService.removeItem(id);
        return ResponseEntity.noContent().build();
    }
}
