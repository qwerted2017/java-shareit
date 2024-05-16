package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item add(Item item);

    Item update(Item item);

    Optional<Item> findItemById(Long itemId);

    List<Item> findAll(User user);

    List<Item> search(String text);
}