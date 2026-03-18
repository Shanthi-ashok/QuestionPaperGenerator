package com.miniproject.qpgenerator.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String role; // ADMIN or FACULTY
}
