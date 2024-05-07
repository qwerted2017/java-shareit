package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    @NotBlank
    @NotNull
    private String name;
    @Email
    @NotBlank
    @NotNull
    private String email;


}