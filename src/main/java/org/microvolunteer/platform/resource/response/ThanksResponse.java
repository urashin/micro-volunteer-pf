package org.microvolunteer.platform.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThanksResponse {
    private String result;
}
