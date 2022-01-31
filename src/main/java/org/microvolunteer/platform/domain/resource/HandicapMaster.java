package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HandicapMaster {
    private Integer handicap_type;
    private String handicap_name;
    private String icon_path;
    private String comment;
}
