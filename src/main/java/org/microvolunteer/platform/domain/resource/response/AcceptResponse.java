package org.microvolunteer.platform.domain.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcceptResponse {
    @JsonProperty("help_id")
    private Integer helpId;
    @JsonProperty("x_geometry")
    private String xGeometry;
    @JsonProperty("y_geometry")
    private String yGeometry;
}
