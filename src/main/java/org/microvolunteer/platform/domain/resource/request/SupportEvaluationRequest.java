package org.microvolunteer.platform.domain.resource.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupportEvaluationRequest {
    private Integer help_id;
    private Integer satisfaction;
    private String comment;
}
