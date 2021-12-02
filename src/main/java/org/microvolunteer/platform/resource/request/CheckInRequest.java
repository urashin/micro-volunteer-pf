package org.microvolunteer.platform.resource.request;

import lombok.Data;

@Data
public class CheckInRequest {
    private String token;  // request header のtokenから取得するよう変更
    private String xGeometry;
    private String yGeometry;
}
