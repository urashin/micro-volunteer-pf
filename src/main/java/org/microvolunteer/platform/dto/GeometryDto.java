package org.microvolunteer.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeometryDto {
    private String x_geometry;
    private String y_geometry;
    public String getPoint() {
        String point = "POINT(" + this.x_geometry + " " + this.y_geometry + ")";
        return point;
    }
}
