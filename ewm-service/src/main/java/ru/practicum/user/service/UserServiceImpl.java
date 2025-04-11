package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(Integer from, Integer size, List<Long> ids) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            users = userRepository.findByIdIn(ids, PageRequest.of(from / size, size)).getContent();
        }
        return users.stream()
                    .map(userMapper::toUserDto)
                    .toList();
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ConflictException("This email is taken");
        }
        User user = userRepository.save(userMapper.toUser(userRequest));
        log.info("User with ID {} has been created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.deleteUserById(id).isPresent()) {
            log.info("User with ID {} has been deleted", id);
        } else {
            log.error("User with ID {} not found", id);
            throw new NotFoundException("User not found");
        }
    }
}
