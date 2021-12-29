package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyActivity {
    private String datetime;
    private String area_name;
    private String icon_path;
    private String satisfaction;

}
