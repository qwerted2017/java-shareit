package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@email.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("desc")
            .owner(user)
            .available(true)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final BookingOutDto bookingOutDto = BookingOutDto.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.WAITING)
            .booker(UserMapper.toUserDto(user))
            .item(ItemMapper.toItemOutDto(item))
            .build();

    @Test
    @SneakyThrows
    void addBookingIsValid() {
        when(bookingService.add(user.getId(), bookingDto)).thenReturn(bookingOutDto);

        String result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutDto), result);
    }

    @Test
    @SneakyThrows
    void bookingCreateFailedByWrongUserId() {
        user.setId(100L);

        bookingDto.setItemId(null);

        when(bookingService.findBookingByUserId(user.getId(), bookingDto.getItemId())).thenReturn(bookingOutDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).add(user.getId(), bookingDto);
    }

    @Test
    @SneakyThrows
    void bookingGetAllWithWrongFrom() {
        Integer from = -1;
        Integer size = 20;

        mockMvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.USER_HEADER, user.getId()))
                .andExpect(status().isInternalServerError());

        verify(bookingService, never()).findAll(user.getId(), "ALL", from, size);
    }

    @Test
    @SneakyThrows
    void booking5setRejectedByUser4() {
        user.setId(4L);
        Long bookingId = 5L;
        boolean approved = false;
        bookingOutDto.setId(5L);

        when(bookingService.update(user.getId(), bookingId, approved)).thenReturn(bookingOutDto);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk());
        verify(bookingService, never()).add(user.getId(), bookingDto);
    }

    @SneakyThrows
    @Test
    void addBookingIncorrectStart() {
        bookingOutDto.setStart(LocalDateTime.now().minusHours(1));

        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookingOutDto))
                                .header(Constants.USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addBookingStartIsNull() {
        bookingOutDto.setStart(null);
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookingOutDto))
                                .header(Constants.USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());
    }


    @SneakyThrows
    @Test
    void addBookingIncorrectPast() {
        bookingOutDto.setEnd(LocalDateTime.now().minusHours(1));

        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(bookingOutDto))
                                .header(Constants.USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        long bookingId = 1L;
        when(bookingService.findBookingByUserId(any(Long.class), any(Long.class))).thenReturn(bookingOutDto);
        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(Constants.USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status").value(bookingOutDto.getStatus().toString()))
                .andExpect(jsonPath("$.item").value(bookingOutDto.getItem()))
                .andExpect(jsonPath("$.booker").value(bookingOutDto.getBooker()));
    }

    @Test
    @SneakyThrows
    void getBookingsOwners() {
        Integer from = 0;
        Integer size = 10;

        when(bookingService.findAllOwner(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingOutDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingOutDto)), result);
    }

    @Test
    @SneakyThrows
    void getAllShouldReturnStatusIsOk() {
        Integer from = 0;
        Integer size = 10;
        String state = "ALL";

        when(bookingService.findAll(user.getId(), BookingState.ALL.toString(), 0, 10))
                .thenReturn(List.of(bookingOutDto));

        String result = mockMvc.perform(get("/bookings")
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingOutDto)), result);
    }

}
