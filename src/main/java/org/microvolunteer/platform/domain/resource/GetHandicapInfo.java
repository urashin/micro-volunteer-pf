package org.microvolunteer.platform.domain.resource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetHandicapInfo {
    private Integer handicapinfo_id;
    private String handicapped_id;
    private Integer handicap_type;
    private Integer handicap_level;
    private Integer reliability_th; // ボランティアの信頼性に対する閾値
    private Integer severity; // 深刻度
    private String comment;
}
