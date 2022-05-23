package org.microvolunteer.platform.domain.resource.response;

import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.HelpSignal;

@Data
@Builder
public class GetSignalResponse {
    private HelpSignal helpSignal;
}
