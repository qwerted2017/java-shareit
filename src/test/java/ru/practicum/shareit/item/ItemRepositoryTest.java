package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("user")
            .email("user@email.com")
            .build();

    private final Item item = Item.builder()
            .name("user")
            .description("desc")
            .available(true)
            .owner(user)
            .build();


    @BeforeEach
    public void addItems() {
        testEntityManager.persist(user);
        testEntityManager.flush();
        itemRepository.save(item);
    }
    @AfterEach
    public void deleteAll() {
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(1L);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "user");
    }

    @Test
    void searchAllByDescription() {
        List<Item> items = itemRepository.search("desc");

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "user");
    }
}

