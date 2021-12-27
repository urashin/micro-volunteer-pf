package org.microvolunteer.platform.domain.resource.snsmessage;

import lombok.Data;

@Data
public class LineLocationMessageResponse {
    private String message;
    private Object response;
    private Integer status;
}
