package org.microvolunteer.platform.domain.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.VolunteerHistory;

import java.util.List;

@Data
@Builder
public class VolunteerHistoryResponse {
    @JsonProperty("volunteer_history")
    private List<VolunteerHistory> volunteerHistory;
}
