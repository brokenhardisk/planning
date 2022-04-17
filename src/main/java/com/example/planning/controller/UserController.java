package com.example.planning.controller;

import com.example.planning.dto.UserRequestDto;
import com.example.planning.dto.UserResponseDto;
import com.example.planning.service.UserAppService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = {"api/planning/user"})
public class UserController {

    @Autowired
    private UserAppService userAppService;

    @GetMapping()
    @ApiOperation(value = "Gets a list of all users",
            response = UserResponseDto.class,
            responseContainer = "List",
            produces = "application/json")
    public ResponseEntity<List<UserResponseDto>> getUserList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "limit", defaultValue = "30") int limit) {
        return ResponseEntity.ok(userAppService.getItemList(page, limit));
    }

    @PostMapping()
    @ApiOperation(value = "Adds a user to the existing list",
            consumes = "application/json",
            response = UserResponseDto.class,
            produces = "application/json")
    public ResponseEntity<UserResponseDto> addUser(@ApiParam(value = "User data") @Valid @RequestBody UserRequestDto item) {
        return new ResponseEntity<>((UserResponseDto) userAppService.addItem(item), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Gets user by id",
            response = UserResponseDto.class,
            produces = "application/json")
    public ResponseEntity<UserResponseDto> getUserById(@ApiParam(value = "User ID", required = true) @PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(userAppService.getItemById(id));
    }

    @GetMapping("/working")
    @ApiOperation(value = "Gets all working users",
            response = UserResponseDto.class,
            responseContainer = "List",
            produces = "application/json")
    public ResponseEntity<List<UserResponseDto>> getAllWorkingUsers() {
        return ResponseEntity.ok(userAppService.getAllWorkingUsers());
    }

    @GetMapping("/idle")
    @ApiOperation(value = "Gets all idle users",
            response = UserResponseDto.class,
            responseContainer = "List",
            produces = "application/json")
    public ResponseEntity<List<UserResponseDto>> getAllIdleUsers() {
        return ResponseEntity.ok(userAppService.getAllNonWorkingUsers());
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "Updates user",
            consumes = "application/json",
            response = UserResponseDto.class,
            produces = "application/json")
    public ResponseEntity<UserResponseDto> updateUser(@ApiParam(value = "User data") @Valid @RequestBody UserRequestDto item,
                                                      @ApiParam(value = "User ID", required = true) @PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(userAppService.updateItem(item, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Deletes a user")
    public ResponseEntity<Void> removeItem(@ApiParam(value = "User ID", required = true) @PathVariable(value = "id") Integer id) {
        userAppService.removeItem(id);
        return ResponseEntity.noContent().build();
    }
}
