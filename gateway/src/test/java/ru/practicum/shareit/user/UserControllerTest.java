package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper mapper;

    private static String PREFIX = "/users";

    @SneakyThrows
    @Test
    void createNewUser_shouldReturnStatus400_whenEmailIsNull() {
        UserDto dtoWithNullEmail = UserDto.builder().name("name").build();

        mockMvc.perform(post(PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoWithNullEmail)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verifyNoInteractions(userClient);
    }


    @SneakyThrows
    @Test
    void findUserById_shouldReturnStatus400_whenIdIsNegative() {
        UserDto dto = UserDto.builder().email("mail@mail.net").name("name").build();

        mockMvc.perform(get(PREFIX + "/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
        verifyNoInteractions(userClient);
    }

    @SneakyThrows
    @Test
    void updateUserFields_shouldReturnOk_whenNameNull() {
        UserDto dto = UserDto.builder().email("any@mail.net").build();
        assertNull(dto.getName());

        mockMvc.perform(patch(PREFIX + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient, atLeastOnce()).updateUserFields(1, dto);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        when(userClient.deleteUserById(anyLong())).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(delete(PREFIX + "/1"))
                .andExpect(status().isNotFound());
    }
}