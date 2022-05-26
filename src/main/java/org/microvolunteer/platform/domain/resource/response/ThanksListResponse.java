package org.microvolunteer.platform.domain.resource.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microvolunteer.platform.domain.resource.DoneThanks;
import org.microvolunteer.platform.domain.resource.SendThanks;
import org.microvolunteer.platform.domain.resource.ThanksList;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanksListResponse {
    private ThanksList thanksList;
    private String result;
    private String message;
}
