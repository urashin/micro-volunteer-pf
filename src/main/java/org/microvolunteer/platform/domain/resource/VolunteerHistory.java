package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VolunteerHistory {
    private String x_geometry;
    private String y_geometry;
    private String datetime;
    private String area_name;
    private Integer handicap_type;
    private Integer handicap_level;
    private Integer satisfaction;
}
