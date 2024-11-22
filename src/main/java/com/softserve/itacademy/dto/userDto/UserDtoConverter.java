package com.softserve.itacademy.dto.userDto;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserDtoConverter {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }

    public void fillFields(User user, UpdateUserDto updateUserDto) {
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
    }

    public void fillFields(User user, CreateUserDto createUserDto) {
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setPassword(createUserDto.getPassword());
        user.setRole(UserRole.USER); //default
        user.setMyTodos(new ArrayList<>()); //to avoid null
        user.setOtherTodos(new ArrayList<>()); //to avoid null
    }

    public UpdateUserDto toUpdateUserDto(User user) {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setId(user.getId());
        updateUserDto.setFirstName(user.getFirstName());
        updateUserDto.setLastName(user.getLastName());
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setRole(user.getRole());

        return updateUserDto;
    }
}
