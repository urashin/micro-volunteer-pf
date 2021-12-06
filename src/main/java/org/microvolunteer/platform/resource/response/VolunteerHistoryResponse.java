package org.microvolunteer.platform.resource.response;

import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.dto.VolunteerHistoryDto;

import java.util.List;

@Data
@Builder
public class VolunteerHistoryResponse {
    private List<VolunteerHistoryDto> volunteerHistory;
}
