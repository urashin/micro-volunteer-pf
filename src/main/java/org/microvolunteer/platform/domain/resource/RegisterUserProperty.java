package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserProperty {
    private String user_id;
    private String name;
    private String password;
    private String email;
    private Integer status;
}
