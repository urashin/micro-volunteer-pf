package org.microvolunteer.platform.domain.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyVolunteerSummary {
    private String my_name;
    private String support_count;
    private String average_satisfaction;
}
