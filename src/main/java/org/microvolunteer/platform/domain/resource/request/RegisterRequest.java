package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String token;
    private String email;
    private String name;
    private String password;
}
