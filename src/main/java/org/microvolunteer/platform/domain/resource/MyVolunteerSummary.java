package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyVolunteerSummary {
    private String my_name;
    private String support_count;
    private String average_satisfaction;
}
