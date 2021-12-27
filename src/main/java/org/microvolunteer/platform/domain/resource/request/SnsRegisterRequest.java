package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class SnsRegisterRequest {
    @NotNull
    @Size(min=5, max=64)
    private String sns_id;

    @Positive
    private Integer sns_type;
}
