package org.microvolunteer.platform.domain.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnetimeurlResponse {
    @JsonProperty("onetimeurl")
    private String onetimeurl;
}
