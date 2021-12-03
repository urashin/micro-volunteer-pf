package org.microvolunteer.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NeighborDistanceDto {
    private String user_id;
    private String x_geometry;
    private String y_geometry;
    private Double distance;
}
