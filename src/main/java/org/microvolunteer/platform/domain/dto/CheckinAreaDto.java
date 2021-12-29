package org.microvolunteer.platform.domain.dto;

import org.microvolunteer.platform.domain.resource.CheckinArea;
import org.microvolunteer.platform.domain.resource.request.CheckinAreaRegisterRequest;

public class CheckinAreaDto {
    public static CheckinArea checkinArea(CheckinAreaRegisterRequest request,String editor_id) {
        return CheckinArea.builder()
                .area_name(request.getArea_name())
                .location(GeometryDto.getPoint(request.getX_geometry(),request.getY_geometry()))
                .radius(request.getRadius())
                .editor_id(editor_id)
                .build();
    }
}
