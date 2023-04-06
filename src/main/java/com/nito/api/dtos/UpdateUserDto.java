package com.nito.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    @NotBlank
    @Size(max = 280)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String username;
}
