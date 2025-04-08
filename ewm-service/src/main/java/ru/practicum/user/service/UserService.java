package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(Integer from, Integer size, List<Long> ids);

    UserDto createUser(NewUserRequest userRequest);

    void deleteUser(Long id);
}
