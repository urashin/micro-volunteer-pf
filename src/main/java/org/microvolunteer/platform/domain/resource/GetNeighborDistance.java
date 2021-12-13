package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetNeighborDistance {
    private String user_id;
    private String x_geometry;
    private String y_geometry;
    private Double distance;
}
