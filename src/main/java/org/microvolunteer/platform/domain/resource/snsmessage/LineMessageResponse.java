package org.microvolunteer.platform.domain.resource.snsmessage;

import lombok.Data;

@Data
public class LineMessageResponse {
    private String message;
    private Object response;
    private Integer status;
}
