package com.tj.GFV_Map.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SendEmailCodeRequestDto {
    @Email
    @NotBlank
    private String email;
}