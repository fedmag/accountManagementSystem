package com.fedmag.accountmanagementsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class EmployeePeriodKey implements Serializable {

  @Column(name = "employee_email")
  private String employeeEmail;

  private LocalDate period;

  public EmployeePeriodKey() {}

  public EmployeePeriodKey(String email, YearMonth periodAsYearMonth) {
    this.employeeEmail = email;
    this.period = periodAsYearMonth.atEndOfMonth();
  }
}
