package org.microvolunteer.platform.domain.resource.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class AcceptRequest {
    @Positive
    private Integer help_id;
}
