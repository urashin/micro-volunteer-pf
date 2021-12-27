package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CheckInRequest {
    @NotNull
    @Size(min=16, max=64)
    private String token;

    private String x_geometry;
    private String y_geometry;
}
