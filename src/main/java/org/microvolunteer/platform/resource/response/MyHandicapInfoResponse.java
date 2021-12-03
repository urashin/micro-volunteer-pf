package org.microvolunteer.platform.resource.response;

import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.dto.HandicapInfoDto;

import java.util.List;

@Data
@Builder
public class MyHandicapInfoResponse {
    List<HandicapInfoDto> handicapInfoDtoList;
}
