package org.microvolunteer.platform.domain.resource.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microvolunteer.platform.domain.resource.MyHandicap;
import org.microvolunteer.platform.domain.resource.MyVolunteerSummary;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileResponse {
    private MyVolunteerSummary volunteer_summary;
    private List<MyHandicap> handicap_list;
}
