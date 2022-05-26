package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class HelpRequest {
    private String x_geometry;
    private String y_geometry;

    @Positive
    private Integer handicapinfo_id;
}
