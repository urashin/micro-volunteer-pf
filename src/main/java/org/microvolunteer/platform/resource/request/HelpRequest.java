package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class HelpRequest {
    private String token;
    private String xGeometry;
    private String yGeometry;
    private Integer helpType;
    private String helpComment;
}
