package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class HelpDetailRequest {
    private String token;
    private Integer helpId;
}
