package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();
    }

    @Test
    void createUser_shouldSaveUser() {
        UserDto created = userService.createUser(userDto);

        assertNotNull(created.getId());
        assertEquals("John Doe", created.getName());
        assertEquals("john@example.com", created.getEmail());
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto created = userService.createUser(userDto);

        UserDto found = userService.getUserById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(created.getName(), found.getName());
    }
}