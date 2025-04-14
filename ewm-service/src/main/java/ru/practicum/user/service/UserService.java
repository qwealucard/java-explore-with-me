package ru.practicum.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    @Transactional(readOnly = true)
    List<UserDto> getUsers(Integer from, Integer size, List<Long> ids);

    @Transactional
    UserDto createUser(NewUserRequest userRequest);

    @Transactional
    void deleteUser(Long id);
}
