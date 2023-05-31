package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @SneakyThrows
    @Test
    void getBookingsOfOwner_shouldReturnClientError_whenInvokedWithWrongState() {
        long ownerId = 3L;
        String state = "WRONG_STATE";
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-id", ownerId)
                        .queryParam("state", state))
                .andExpect(status().is4xxClientError());
    }
}