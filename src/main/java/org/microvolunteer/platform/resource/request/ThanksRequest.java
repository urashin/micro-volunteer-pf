package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class ThanksRequest {
    private String token;
    private Integer help_id;
    private Integer evaluate;
}
