package org.microvolunteer.platform.domain.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerHistory {
    private String x_geometry;
    private String y_geometry;
    private String datetime;
    private String area_name;
    private Integer handicap_type;
    private Integer handicap_level;
    private Integer satisfaction;
}
