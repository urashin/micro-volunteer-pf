package org.microvolunteer.platform.domain.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microvolunteer.platform.domain.resource.MyHandicap;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyHandicapListResponse {
    @JsonProperty("handicap_info_list")
    List<MyHandicap> handicapInfoList;
}
