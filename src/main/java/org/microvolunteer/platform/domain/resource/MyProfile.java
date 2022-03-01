package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyProfile {
    private MyVolunteerSummary volunteer_summary;
    private List<MyHandicap> handicap_list;
}
