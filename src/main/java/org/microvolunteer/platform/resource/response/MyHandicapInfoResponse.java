package org.microvolunteer.platform.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.dto.HandicapInfoDto;

import java.util.List;

@Data
@Builder
public class MyHandicapInfoResponse {
    @JsonProperty("handicap_info_list")
    List<HandicapInfoDto> handicapInfoDtoList;
}
