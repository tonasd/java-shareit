/*
package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    static String user1Json;


    @BeforeAll
    static void beforeAll() {
        user1Json = "{ " +
                "    \"name\": \"user\", " +
                "    \"email\": \"user@user.com\" " +
                "}";
    }

    @BeforeEach
    void setUp() {
        user1Json = "{ " +
                "    \"name\": \"user\", " +
                "    \"email\": \"user@user.com\" " +
                "}";
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createNewUser() throws Exception {
        User user = objectMapper.readValue(user1Json, User.class);
        user.setId(1L);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user1Json)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()));
    }

    @Test
    void getAll() {
    }

    @Test
    void findUserById() {
    }

    @Test
    void updateUserFields() {
    }

    @Test
    void deleteUser() {
    }
}*/
