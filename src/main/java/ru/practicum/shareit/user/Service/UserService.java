package ru.practicum.shareit.user.Service;


import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long userId);

    UserDto updateUser(UserDto userDto, Long userId);

    void deleteUser(Long userId);
}
