package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.DuplicateEmail;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long generatorId = 1L;

    @Override
    public User add(User user) {
        if (emails.contains(user.getEmail())) {
            throw new DuplicateEmail(String.format("Email %s is assigned to another user", user.getEmail()));
        }
        user.setId(generatorId);
        generatorId++;

        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(Long id, User user) {
        findUserInMemory(id);
        String existEmail = findById(id).getEmail();
        emails.remove(existEmail);
        if (emails.contains(user.getEmail())) {
            emails.add(existEmail);
            throw new DuplicateEmail(String.format("Email %s is assigned to another user", user.getEmail()));
        }
        emails.add(user.getEmail());

        users.put(id, user);
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        findUserInMemory(id);
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        findUserInMemory(id);
        emails.remove(findById(id).getEmail());
        users.remove(id);
    }

    private void findUserInMemory(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found!");
        }
    }
}