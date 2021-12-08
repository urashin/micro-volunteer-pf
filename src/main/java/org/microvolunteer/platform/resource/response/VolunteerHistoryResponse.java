package org.microvolunteer.platform.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.dto.VolunteerHistoryDto;

import java.util.List;

@Data
@Builder
public class VolunteerHistoryResponse {
    @JsonProperty("volunteer_history")
    private List<VolunteerHistoryDto> volunteerHistory;
}
