package com.example.automaticbookingbot.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
