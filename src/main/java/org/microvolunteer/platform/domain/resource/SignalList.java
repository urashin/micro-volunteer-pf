package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SignalList {
    List<HelpSignal> help_signals;
}
