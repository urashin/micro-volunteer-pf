package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class LineLoginRequest {
    private String client_id;
    private String client_secret;
}
