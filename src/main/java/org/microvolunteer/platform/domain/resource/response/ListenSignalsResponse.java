package org.microvolunteer.platform.domain.resource.response;

import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.HelpSignal;

import java.util.List;

@Data
@Builder
public class ListenSignalsResponse {
    private List<HelpSignal> helpSignals;
}
