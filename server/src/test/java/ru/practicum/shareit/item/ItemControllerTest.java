package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsSecondArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    @SneakyThrows
    @Test
    void createNewItem_shouldReturnStatusCreatedAndCorrectBody_whenInvoked() {
        long id = 1;
        ItemDto dto = ItemDto.builder().name("name").available(true).build();
        long userId = 123;
        when(service.create(userId, dto)).thenReturn(dto.toBuilder().id(id).build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.available", Matchers.is(true)));
    }

    @SneakyThrows
    @Test
    void createNewItem_shouldReturnNotFound_whenServiceThrowUserNotFoundException() {
        ItemDto dto = ItemDto.builder().name("name").available(true).build();
        when(service.create(anyLong(), any(ItemDto.class))).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateItemFields_shouldReturnStatusOkAndCorrectBody_whenInvoked() {
        long requestId = 3;
        ItemDto dto = ItemDto.builder().requestId(requestId).build();
        long itemId = 2;
        long userId = 1;
        when(service.update(userId, dto.toBuilder().id(itemId).build()))
                .then(returnsSecondArg());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.requestId", is(requestId), Long.class));
    }

    @SneakyThrows
    @Test
    void getByItemId_shouldReturnStatusOkAndCorrectBody_whenInvoked() {
        long itemId = 2;
        long userId = 1;

        ItemWithBookingsAndCommentsDto expected = new ItemWithBookingsAndCommentsDto();
        expected.setId(itemId);
        expected.setDescription("description");
        expected.setComments(List.of(
                new CommentDto(1, "text1", "name1", LocalDateTime.now().minusNanos(100)),
                new CommentDto(2, "text2", "name2", LocalDateTime.now())
        ));
        when(service.getByItemId(itemId, userId)).thenReturn(expected);

        String responseBody = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expected), responseBody);

    }

    @Test
    void getAllUsersItems_shouldReturnOk_whenInvoked() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("size", "30")
                        .queryParam("from", "60"))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    @Test
    void findByText_shouldReturnOk_whenInvoked() {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("text", "anyText"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void postCommentForItem_shouldReturnBadRequest_whenNoTextPropertyInBody() {
        String contentWithoutTextProperty = mapper.writeValueAsString(Map.of("noTextProperty", "anyText"));

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentWithoutTextProperty)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Body must have not empty text property"));
    }

    @SneakyThrows
    @Test
    void postCommentForItem_shouldReturnOk_whenInvokedCorrectly() {
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"anyText\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}