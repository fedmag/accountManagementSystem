package com.fedmag.accountmanagementsystem.common.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.YearMonth;

public record PaymentRequest(
    String employee,
    @JsonFormat(pattern = "MM-yyyy") YearMonth period,
    Long salary
) {
}
