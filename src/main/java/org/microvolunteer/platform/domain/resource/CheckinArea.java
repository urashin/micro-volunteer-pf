package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckinArea {
    private String area_name;
    private String location;
    private Integer radius;
    private String editor_id;
}
