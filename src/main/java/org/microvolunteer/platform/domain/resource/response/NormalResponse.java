package org.microvolunteer.platform.domain.resource.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NormalResponse {
    private String result;
}
