package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class HelpDetailRequest {
    private String token;
    private Integer help_id;
}
