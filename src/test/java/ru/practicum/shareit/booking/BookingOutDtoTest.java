package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOutDtoTest {

    @Autowired
    private JacksonTester<BookingOutDto> json;

    private static final String DATE_TIME = "2024-06-06T11:12:00";

    private BookingOutDto bookingOutDto = null;

    private final User user = User.builder().id(1L).name("user").email("user@email.ru").build();

    private final Item item = Item.builder().id(1L).name("item").description("desc").available(true).owner(user).build();

    private final ItemOutDto itemDto = ItemMapper.toItemOutDto(item);

    @BeforeEach
    public void init() {

        bookingOutDto = BookingOutDto.builder().id(1L).item(itemDto).start(LocalDateTime.parse("2024-06-06T11:12:00")).end(LocalDateTime.parse("2024-06-06T11:12:00")).booker(UserMapper.toUserDto(user)).build();
    }

    @Test
    @SneakyThrows
    public void startSerializes() {
        assertThat(json.write(bookingOutDto)).extractingJsonPathStringValue("$.start").isEqualTo(DATE_TIME);
    }

    @Test
    @SneakyThrows
    public void endSerializes() {
        assertThat(json.write(bookingOutDto)).extractingJsonPathStringValue("$.end").isEqualTo(DATE_TIME);
    }
}
