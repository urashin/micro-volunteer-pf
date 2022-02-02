package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendThanks {
    private Integer help_id;
    private String handicap_name;
    private String handicap_type;
    private String datetime;
    private String area_name;
    private String satisfaction;
}
