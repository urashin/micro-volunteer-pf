package org.microvolunteer.platform.domain.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyHandicap {
    private Integer handicapinfo_id;
    private String handicap_name;
    private String handicap_type;
    private Integer handicap_level;
    private Integer reliability_th; // ボランティアの信頼性に対する閾値
    private Integer severity; // 深刻度
    private String comment;
}
