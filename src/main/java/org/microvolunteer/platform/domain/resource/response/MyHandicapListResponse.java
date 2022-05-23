package org.microvolunteer.platform.domain.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.MyHandicap;

import java.util.List;

@Data
@Builder
public class MyHandicapListResponse {
    @JsonProperty("handicap_info_list")
    List<MyHandicap> handicapInfoList;
}
