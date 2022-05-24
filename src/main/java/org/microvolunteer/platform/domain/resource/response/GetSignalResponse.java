package org.microvolunteer.platform.domain.resource.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microvolunteer.platform.domain.resource.HelpSignal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSignalResponse {
    private HelpSignal helpSignal;
}
