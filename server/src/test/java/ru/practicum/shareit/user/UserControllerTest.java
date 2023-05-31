package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    @Test
    void createNewUser_shouldHaveStatusCreatedAndReturnObject_whenInvokedCorrectly() {
        UserDto dto = UserDto.builder().name("name").email("mail@mail.net").build();
        when(service.create(dto)).thenReturn(dto);

        String responseBody = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(dto), responseBody);
    }

    @SneakyThrows
    @Test
    void createNewUser_shouldHaveStatusConflict_whenServiceThrowsDataIntegrityViolationException() {
        UserDto dto = UserDto.builder().name("name").email("repeatedEmail@mail.net").build();
        when(service.create(dto)).thenThrow(new DataIntegrityViolationException("EMAIL_UNIQUE violation"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }


    @SneakyThrows
    @Test
    void getAll_shouldReturnOk_whenInvoked() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(service, times(1)).getAll();
    }

    @SneakyThrows
    @Test
    void findUserById_shouldPassParamIntoServiceAndReturnStatusOk_whenInvoked() {
        long userId = 123;

        mockMvc.perform(get("/users/{userId}", userId))
                        .andExpect(status().isOk());

        verify(service, atLeastOnce()).get(userId);
    }

    @SneakyThrows
    @Test
    void updateUserFields_shouldReturnStatusOk_whenInvoked() {
        long userId = 123L;
        UserDto dto = UserDto.builder().id(null).email("mail@email.net").name("name").build();
        when(service.updateUserFields(any(UserDto.class))).then(AdditionalAnswers.returnsFirstArg());

        mockMvc.perform(patch("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class));
    }

    @SneakyThrows
    @Test
    void deleteUser_shouldInvokeDeleteServiceMetod_whenInvoked() {
        long userId = 123;
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(service, times(1)).delete(userId);
        verifyNoMoreInteractions(service);
    }
}