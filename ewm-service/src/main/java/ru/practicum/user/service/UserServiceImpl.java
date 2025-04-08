package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(Integer from, Integer size, List<Long> ids) {
        if(ids == null) {
            List<User> users = userRepository.findAll(PageRequest.of(from / size, size)).toList();
            return userMapper.toUserDto(users);
        }
         List<UserDto> userDtos = new ArrayList<>();
        for(Long id : ids) {
            User user = userRepository.findById(id).orElseThrow(() -> {
                log.error("User with ID {} not found", id);
                return new NotFoundException("User not found");
            });
            userDtos.add(userMapper.toUserDto(user));
        }
        return userDtos;
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userRequest) {
       User user = userRepository.save(userMapper.toUser(userRequest));
       log.info("User with ID {} has been created", user.getId());
       return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id);
        userRepository.deleteById(id);
        log.info("User with ID {} has been deleted", id);
    }
}
