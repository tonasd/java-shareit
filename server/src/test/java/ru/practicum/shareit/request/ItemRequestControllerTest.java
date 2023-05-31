package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;
    @Captor
    private ArgumentCaptor<AddItemRequestDto> addItemRequestDtoArgumentCaptor;

    @SneakyThrows
    @Test
    void createShouldPassCorrectDescriptionAndRequesterIdAndStatusIsOk() {
        AddItemRequestDto itemRequestDtoToCreate = new AddItemRequestDto();
        itemRequestDtoToCreate.setDescription("some description");
        long requesterId = 1L;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-id", requesterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDtoToCreate)))
                .andExpect(status().isOk());
        verify(itemRequestService, times(1)).create(addItemRequestDtoArgumentCaptor.capture());
        AddItemRequestDto actual = addItemRequestDtoArgumentCaptor.getValue();
        assertEquals(requesterId, actual.getRequesterId());
        assertEquals("some description", actual.getDescription());
    }

    @SneakyThrows
    @Test
    void findAllRequesterRequestsShouldInvokeServiceMethodWIthCorrectId() {
        long requesterId = 1L;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-id", requesterId))
                .andExpect(status().isOk());

        verify(itemRequestService).findAllRequesterRequests(requesterId);
        verifyNoMoreInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getAllPageableShouldReturnCorrectList() {
        long requesterId = 1L;
        Integer from = 0;
        Integer size = 3;
        List<ItemRequestWithItemsDto> expectedList = List.of(new ItemRequestWithItemsDto(), new ItemRequestWithItemsDto());

        when(itemRequestService.findAllPageable(requesterId, from, size))
                .thenReturn(expectedList);

        String responseBody = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-id", requesterId)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedList), responseBody);
    }

    @SneakyThrows
    @Test
    void getRequestByIdShouldInvokeServiceMethodWithCorrectParams() {
        long requestId = 5;
        long userId = 1;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-id", userId))
                .andExpect(status().isOk());

        verify(itemRequestService).getRequestById(userId, requestId);
        verifyNoMoreInteractions(itemRequestService);
    }
}