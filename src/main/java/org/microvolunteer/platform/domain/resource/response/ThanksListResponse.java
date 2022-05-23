package org.microvolunteer.platform.domain.resource.response;

import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.DoneThanks;
import org.microvolunteer.platform.domain.resource.SendThanks;
import org.microvolunteer.platform.domain.resource.ThanksList;

import java.util.List;

@Data
@Builder
public class ThanksListResponse {
    private ThanksList thanksList;
    private String result;
    private String message;
}
