package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterHelp {
    private String handicapped_id;
    private String volunteer_id;
    private Integer handicapinfo_id;
    private Integer handicap_type;
    private Integer handicap_level;
    private Integer reliability_th;
    private Integer severity;
    private String location;
    private String comment;
    private Integer status;
}
