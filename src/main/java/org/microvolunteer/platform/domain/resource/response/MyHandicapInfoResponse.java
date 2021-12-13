package org.microvolunteer.platform.domain.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.HandicapInfo;

import java.util.List;

@Data
@Builder
public class MyHandicapInfoResponse {
    @JsonProperty("handicap_info_list")
    List<HandicapInfo> handicapInfoList;
}
