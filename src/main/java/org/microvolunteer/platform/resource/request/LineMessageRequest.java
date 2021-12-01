package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class LineMessageRequest {
    private String snsId;
    private String message;
}
