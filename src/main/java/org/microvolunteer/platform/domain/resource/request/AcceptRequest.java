package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class AcceptRequest {
    private String token;
    private Integer help_id;
}
