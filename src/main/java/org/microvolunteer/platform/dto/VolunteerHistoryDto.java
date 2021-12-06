package org.microvolunteer.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VolunteerHistoryDto {
    private String x_geometry;
    private String y_geometry;
    private Integer handicap_type;
    private Integer handicap_level;
    private Integer satisfaction;
}
