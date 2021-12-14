package org.microvolunteer.platform.domain.resource.snsmessage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LineMessageRequest {
    private String sns_id;
    private String message;
}
