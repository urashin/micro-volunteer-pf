package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String user_id;
    private String password;
}
