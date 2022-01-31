package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class AddHandicapTypeRequest {
    private String auth_code;
    private String handicap_name;
    private String icon_path;
    private String comment;
}
