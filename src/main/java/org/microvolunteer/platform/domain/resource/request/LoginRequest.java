package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
