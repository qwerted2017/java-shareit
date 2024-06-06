package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.Constants.USER_HEADER;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@email.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("desc")
            .available(true)
            .owner(user)
            .build();

    private final ItemOutDto itemDto = ItemMapper.toItemOutDto(item);


    @Test
    @SneakyThrows
    void addValidItem() {

        when(itemService.add(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(USER_HEADER, user.getId())
                                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    @SneakyThrows
    void updateWhenItemIsValidShouldReturnStatusIsOk() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDto itemDtoToCreate = ItemDto.builder()
                .description("some item description")
                .name("some item name")
                .available(true)
                .build();

        when(itemService.update(userId, itemId, itemDtoToCreate))
                .thenReturn(ItemMapper.toItemOutDto(ItemMapper.toItem(itemDtoToCreate)));

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void getItem() {
        when(itemService.findItemById(user.getId(), item.getId())).thenReturn(itemDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @Test
    @SneakyThrows
    void getAllShouldReturnStatusOk() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        List<ItemOutDto> itemsDtoToExpect = List.of(ItemOutDto.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemService.findAll(userId)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", from, size)
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }

    @Test
    @SneakyThrows
    void searchItemsShouldReturnStatusOk() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        String text = "find";
        List<ItemDto> itemsDtoToExpect = List.of(ItemDto.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemService.search(userId, text)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }

    @Test
    @SneakyThrows
    void createValidComment() {
        ItemOutDto itemDtoOut = itemService.add(user.getId(), ItemMapper.toItemDto(item));
        CommentDto commentToAdd = CommentDto.builder()
                .text("some comment")
                .build();
        CommentOutDto commentDtoOut = CommentOutDto.builder()
                .id(1L)
                .itemId(item.getId())
                .text(commentToAdd.getText())
                .build();
        when(itemService.createComment(user.getId(), commentToAdd, item.getId())).thenReturn(commentDtoOut);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(commentToAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDtoOut), result);
    }
}
