package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private static final String DATE_TIME = "2024-06-06T11:12:00";

    private BookingDto bookingDto = null;

    @BeforeEach
    public void init() {
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2024-06-06T11:12:00"))
                .end(LocalDateTime.parse("2024-06-06T11:12:00"))
                .build();
    }

    @Test
    @SneakyThrows
    public void startSerializes() {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    @SneakyThrows
    public void endSerializes() {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
