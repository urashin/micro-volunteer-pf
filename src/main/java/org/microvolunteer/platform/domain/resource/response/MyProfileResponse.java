package org.microvolunteer.platform.domain.resource.response;

import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.MyHandicap;
import org.microvolunteer.platform.domain.resource.MyVolunteerSummary;

import java.util.List;

@Data
@Builder
public class MyProfileResponse {
    private MyVolunteerSummary volunteer_summary;
    private List<MyHandicap> handicap_list;
}
