package org.microvolunteer.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {
    private String x_geometry;
    private String y_geometry;
}
