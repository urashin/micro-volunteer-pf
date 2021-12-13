package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

@Data
public class HandicapRegisterRequest {
    private String token;
    private Integer handicap_type;
    private Integer handicap_level;
    private Integer reliability_th; // ボランティアの信頼性に対する閾値
    private Integer severity; // 深刻度
    private String comment;
}
