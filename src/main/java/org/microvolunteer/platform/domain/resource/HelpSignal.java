package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HelpSignal {
    private Integer help_id;
    private String area_name;
    private String handicap_name;
    private String x_geometory;
    private String y_geometory;
    private Integer distance;
    private Integer handicap_type;
    private Integer handicap_level;
    private String comment;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
