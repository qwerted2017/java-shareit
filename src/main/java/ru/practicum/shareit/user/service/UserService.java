package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto findById(Long id) {
        if (!isUserExists(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
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
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private boolean isUserExists(Long userId) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}