package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private String x_geometry;
    private String y_geometry;
}
