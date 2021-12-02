package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String password;
}
