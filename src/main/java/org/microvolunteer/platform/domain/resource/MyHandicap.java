package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyHandicap {
    private String handicap_name;
    private String handicap_type;
    private Integer handicap_level;
    private Integer reliability_th; // ボランティアの信頼性に対する閾値
    private Integer severity; // 深刻度
    private String comment;
}
