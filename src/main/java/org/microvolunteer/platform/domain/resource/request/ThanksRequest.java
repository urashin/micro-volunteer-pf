package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class ThanksRequest {
    @Positive
    private Integer help_id;

    @Positive
    private Integer evaluate;

    private String comment;
}
