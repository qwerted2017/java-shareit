package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User update(Long id, User user);

    List<User> findAll();

    User findById(Long id);

    void delete(Long id);
}