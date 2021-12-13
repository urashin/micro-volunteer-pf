package org.microvolunteer.platform.domain.dto;

public class GeometryDto {
    public static String getPoint(String x_geometry, String y_geometry) {
        String point = "POINT(" + x_geometry + " " + y_geometry + ")";
        return point;
    }
}
