package org.microvolunteer.platform.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HelpDto {
    private String handicapped_id;
    private String volunteer_id;
    private Integer handicapinfo_id;
    private Integer reliability_th;
    private Integer severity;
    private GeometryDto help_geometry;
    private String location;
    private String comment;
    private Integer status;
}
