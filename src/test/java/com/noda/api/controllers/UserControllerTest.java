package com.noda.api.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.noda.api.dtos.AddressRequestDTO;
import com.noda.api.dtos.UserRequestDTO;
import com.noda.api.exceptions.UserNotFoundException;
import com.noda.api.models.User;
import com.noda.api.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User buildMockUser() {
        User user = new User();
        user.setId(1L);
        user.setName("eren");
        user.setCpf("11122233344");
        user.setEmail("eren@gmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setAddress(null);
        return user;
    }

    @Nested
    @DisplayName("POST /users")
    class CreateUser {

        @Test
        @DisplayName("Should return 201 and user body when data is valid")
        void shouldReturn201WhenDataIsValid() throws Exception {

            AddressRequestDTO address = new AddressRequestDTO("33666555", "187", null);
            UserRequestDTO request = new UserRequestDTO(
                    "eren", "11122233344", "eren@gmail.com",
                    "Senha@123", LocalDate.of(2000, 1, 1), address
            );

            Mockito.when(userService.save(Mockito.any())).thenReturn(buildMockUser());

            mockMvc.perform(
                            post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("eren"))
                    .andExpect(jsonPath("$.cpf").value("11122233344"))
                    .andExpect(jsonPath("$.email").value("eren@gmail.com"));
        }

        @Test
        @DisplayName("Should return 400 when request body is invalid")
        void shouldReturn400WhenBodyIsInvalid() throws Exception {

            mockMvc.perform(
                            post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}")
                    )
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserById {

        @Test
        @DisplayName("Should return 200 and user body when user exists")
        void shouldReturn200WhenUserExists() throws Exception {

            Mockito.when(userService.findUserById(1L)).thenReturn(buildMockUser());

            mockMvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("eren"))
                    .andExpect(jsonPath("$.email").value("eren@gmail.com"));
        }

        @Test
        @DisplayName("Should return 404 when user does not exist")
        void shouldReturn404WhenUserNotFound() throws Exception {

            Mockito.when(userService.findUserById(99L))
                    .thenThrow(new UserNotFoundException("User not found"));

            mockMvc.perform(get("/users/99"))
                    .andExpect(status().isNotFound());
        }
    }
}