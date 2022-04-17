package com.example.planning.service.impl;

import com.example.planning.dto.UserRequestDto;
import com.example.planning.dto.UserResponseDto;
import com.example.planning.exception.ApplicationException;
import com.example.planning.exception.ItemNotFoundException;
import com.example.planning.persistence.model.User;
import com.example.planning.persistence.repository.ShiftRepository;
import com.example.planning.persistence.repository.UserRepository;
import com.example.planning.persistence.repository.WorkdayRepository;
import com.example.planning.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userAppService")
public class UserAppServiceImpl implements UserAppService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private WorkdayRepository workdayRepository;

    private static final String ITEM_ALREADY_EXISTS = "User already exists. Try updating the shift ";
    private static final String ITEM_NOT_FOUND = "User with given id does not exists: ";

    @Override
    public List<UserResponseDto> getAllWorkingUsers() {
        List<UserResponseDto> resultList = new ArrayList<>();
        Optional.ofNullable(userRepository.findByShiftIdIsNotNull())
                .ifPresent(res -> res.get()
                        .forEach(item -> resultList.add(mapToUserResponseDto(item))));
        return resultList;
    }

    @Override
    public List<UserResponseDto> getAllNonWorkingUsers() {

        List<UserResponseDto> resultList = new ArrayList<>();
        Optional.ofNullable(userRepository.findByShiftIdIsNull())
                .ifPresent(res -> res.get()
                        .forEach(item -> resultList.add(mapToUserResponseDto(item))));
        return resultList;
    }

    @Override
    public List<UserResponseDto> getItemList(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        List<UserResponseDto> resultList = new ArrayList<>();
        Optional.ofNullable(userRepository.findAll(pageableRequest))
                .ifPresent(res -> res.getContent()
                        .forEach(item -> resultList.add(mapToUserResponseDto(item))));
        return resultList;
    }

    @Override
    public UserResponseDto addItem(UserRequestDto item) {
        /* Not checking the case here*/
        boolean userExists = userRepository.findByName(item.getName()).isPresent();
        if (userExists) {
            throw new ApplicationException(ITEM_ALREADY_EXISTS + item.getName());
        }
        if (item.getShiftId() != null) {
            shiftRepository.findById(item.getShiftId())
                    .orElseThrow(() -> new ItemNotFoundException("Shift does not exists for id " + item.getShiftId()));
        }
        User newUser = User.builder()
                .name(item.getName())
                .shiftId(item.getShiftId())
                .lastUpdate(LocalDateTime.now())
                .build();
        User createdUser = userRepository.save(newUser);
        return mapToUserResponseDto(createdUser);
    }

    @Override
    public UserResponseDto getItemById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + id));
        return mapToUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateItem(UserRequestDto item, Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + id));

        Optional<User> userByName = userRepository.findByName(item.getName());

        // if another user with the same name already exists, then throw an exception
        if (userByName.isPresent() && !userByName.get().getId().equals(id)) {
            throw new ApplicationException(ITEM_ALREADY_EXISTS + item.getName());
        }
        if (item.getShiftId() != null) {
            shiftRepository.findById(item.getShiftId())
                    .orElseThrow(() -> new ItemNotFoundException("Shift does not exists for id " + item.getShiftId()));
        }
        user.setName(item.getName());
        user.setShiftId(item.getShiftId());
        user.setLastUpdate(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return mapToUserResponseDto(updatedUser);
    }

    @Override
    public void removeItem(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + id));
        userRepository.delete(user);
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .shiftId(user.getShiftId())
                .build();
    }
}
