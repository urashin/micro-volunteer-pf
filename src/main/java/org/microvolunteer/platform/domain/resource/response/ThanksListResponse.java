package org.microvolunteer.platform.domain.resource.response;

import lombok.Builder;
import lombok.Data;
import org.microvolunteer.platform.domain.resource.DoneThanks;
import org.microvolunteer.platform.domain.resource.SendThanks;

import java.util.List;

@Data
@Builder
public class ThanksListResponse {
    private List<SendThanks> sendList;
    private List<DoneThanks> doneList;
}
