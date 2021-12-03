package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String user_id;
    private String password;
}
