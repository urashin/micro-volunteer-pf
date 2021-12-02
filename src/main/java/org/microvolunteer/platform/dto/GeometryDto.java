package org.microvolunteer.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeometryDto {
    private String xGeometry;
    private String yGeometry;
    public String getPoint() {
        String point = "POINT(" + this.xGeometry + " " + this.yGeometry + ")";
        return point;
    }
}
