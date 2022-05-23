package org.microvolunteer.platform.domain.resource.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ListenRequest {
    private Integer area_id;
    private String x_geometry;
    private String y_geometry;
    private Integer radius;
}
