package org.microvolunteer.platform.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HelpDetailResponse {
    private Integer helpId;
    private String xGeometry;
    private String yGeometry;
    private Integer helpType;
    private String helpComment;
}
