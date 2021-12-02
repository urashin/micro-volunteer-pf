package org.microvolunteer.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPropertyDto {
    private String user_id;
    private String name;
    private String password;
    private String email;
    private Integer status;
}
