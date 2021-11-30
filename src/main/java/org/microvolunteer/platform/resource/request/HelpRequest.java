package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class HelpRequest {
    private String userId; // request header のtokenから取得するよう変更
    private String xGeometry;
    private String yGeometry;
    private Integer helpType;
    private String helpComment;
}
