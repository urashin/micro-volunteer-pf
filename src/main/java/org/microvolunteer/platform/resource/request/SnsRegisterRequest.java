package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class SnsRegisterRequest {
    private String sns_id;
    private Integer sns_type;
}
