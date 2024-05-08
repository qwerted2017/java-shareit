package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final Map<User, List<Item>> items = new HashMap<>();
    private Long generatorId = 1L;

    @Override
    public Item add(Item item) {
        item.setId(generatorId);
        generatorId++;
        List<Item> listItems = new ArrayList<>();
        listItems.add(item);
        items.put(item.getOwner(), listItems);
        return item;
    }

    @Override
    public Item update(Item item) {
        List<Item> userItems = items.get(item.getOwner());
        List<Item> delete = userItems.stream()
                .filter(item1 -> item1.getId().equals(item.getId()))
                .collect(Collectors.toList());

        userItems.removeAll(delete);
        userItems.add(item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public List<Item> findAll(User user) {
        return new ArrayList<>(items.get(user));
    }

    @Override
    public List<Item> search(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) || item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}