package org.sanedge.domain.request.auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String password;
}
