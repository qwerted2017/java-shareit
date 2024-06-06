package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    private static final String name = "user";
    private static final Boolean available = true;
    private static final String description = "desc";

    private ItemDto itemDto = null;

    @BeforeEach
    public void init() {
        itemDto = ItemDto.builder()
                .name(name)
                .available(available)
                .description(description)
                .build();
    }

    @Test
    @SneakyThrows
    public void itemDtoNameSerialize() {
        assertThat(json.write(itemDto))
                .extractingJsonPathStringValue("$.name").isEqualTo(name);
    }

    @Test
    @SneakyThrows
    public void itemDtoaAvailableSerialize() {
        assertThat(json.write(itemDto))
                .extractingJsonPathBooleanValue("$.available").isEqualTo(available);
    }

    @Test
    @SneakyThrows
    public void itemDtoDescriptionSerialize() {
        assertThat(json.write(itemDto))
                .extractingJsonPathStringValue("$.description").isEqualTo(description);
    }
}
