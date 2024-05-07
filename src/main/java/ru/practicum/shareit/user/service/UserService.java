package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.add(user));
    }

    public UserDto findById(Long id) {
        if (!isUserExists(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        User user = userStorage.findById(id);
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto update(Long id, UserDto userDto) {
        if (!isUserExists(id)) {
            throw new NotFoundException("User with id " + id + " not found!");
        }

        User user = new User();
        UserDto existingUser = findById(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        } else {
            user.setName(existingUser.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        } else {
            user.setEmail(existingUser.getEmail());
        }
        user.setId(id);
        return UserMapper.toUserDto(userStorage.update(id, user));
    }

    public void delete(Long id) {
        if (isUserExists(id)) {
            userStorage.delete(id);
        } else {
            throw new NotFoundException("User with id " + id + " not found!");
        }
    }

    private boolean isUserExists(Long userId) {
        return userStorage.findAll().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}