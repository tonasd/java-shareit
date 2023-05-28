package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService service;

    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    @Test
    void create_shouldReturnClientError_whenHeaderWithoutUserIdInfo() {
        BookingCreationDto dto = new BookingCreationDto();

        mockMvc.perform(post("/bookings")
                        .header("OtherHeader", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(service);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnOkAndCorrectObject_whenInvoked() {
        BookingCreationDto dto = new BookingCreationDto();
        long bookerId = 2L;
        BookingDto expected = new BookingDto(2, "2022", "2023", "Status", null, null);
        when(service.create(any(BookingCreationDto.class))).thenReturn(expected);

        String responseBody = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertEquals(mapper.writeValueAsString(expected), responseBody);

        dto.setBookerId(bookerId);
        verify(service, times(1)).create(dto);

    }

    @SneakyThrows
    @Test
    void ownerAcceptation_shouldPassAllParamsToServiceMethod_whenInvoked() {
        long ownerId = 5L;
        long bookingId = 4L;
        boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-id", ownerId)
                        .queryParam("approved", Boolean.toString(approved))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).ownerAcceptation(bookingId, ownerId, approved);

    }

    @SneakyThrows
    @Test
    void getBookingByOwnerOrBooker_shouldReturnOk_whenInvoked() {
        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-id", 1))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findBookingsOfBooker_shouldReturnCorrectCollection_whenInvoked() {
        DateTimeFormatter frmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        long bookerId = 3L;
        BookingSearchState state = BookingSearchState.APPROVED;
        int from = 10;
        int size = 5;
        List<BookingDto> expected = List.of(
                new BookingDto(1,
                        LocalDateTime.now().minusDays(1).format(frmt),
                        LocalDateTime.now().minusHours(23).format(frmt),
                        BookingStatus.APPROVED.name(),
                        null,
                        null),
                new BookingDto(3,
                        LocalDateTime.now().minusDays(2).format(frmt),
                        LocalDateTime.now().minusHours(47).format(frmt),
                        BookingStatus.APPROVED.name(),
                        null,
                        null)
        );
        when(service.findAllBookingsOfBooker(bookerId, state, from, size))
                .thenReturn(expected);

        String responseBody = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-id", bookerId)
                        .queryParam("state", state.name())
                        .queryParam("from", Integer.toString(from))
                        .queryParam("size", Integer.toString(size)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertArrayEquals(expected.toArray(),
                mapper.readValue(responseBody, BookingDto[].class));

        verify(service, times(1))
                .findAllBookingsOfBooker(bookerId, state, from, size);

    }

    @SneakyThrows
    @Test
    void findBookingsOfOwner_shouldPassAllDefaultParamsToServiceMethod_whenInvokedWithoutParams() {
        long ownerId = 3L;
        BookingSearchState defaultState = BookingSearchState.ALL;
        int defaultFrom = 0;
        int defaultSize = 10;
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-id", ownerId))
                .andExpect(status().isOk());

        verify(service, times(1))
                .findAllBookingsOfOwner(ownerId, defaultState, defaultFrom, defaultSize);

    }
}