package com.backend.dorandoran.counsel.domain.response;

import java.util.List;

public record SuggestHospitalResponse(
        boolean suggestVisit,
        String comment,
        List<String> phoneNumbers
) {
}
