package org.microvolunteer.platform.domain.resource.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class AcceptRequest {
    @NotNull
    @Size(min=16, max=64)
    private String token;

    @Positive
    private Integer help_id;
}
