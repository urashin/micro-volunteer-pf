package org.microvolunteer.platform.domain.resource.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
public class HandicapRegisterRequest {
    @Positive
    private Integer handicap_type;

    @Positive
    private Integer handicap_level;

    @Positive
    private Integer reliability_th; // ボランティアの信頼性に対する閾値

    @Positive
    private Integer severity; // 深刻度

    private String comment;
}
