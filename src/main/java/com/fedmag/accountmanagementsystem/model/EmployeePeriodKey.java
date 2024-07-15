package com.fedmag.accountmanagementsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;

@Embeddable
public class EmployeePeriodKey implements Serializable {

  @Column(name = "employee_email")
  private String employeeEmail;
  private LocalDate period;

  public EmployeePeriodKey() {
  }

  public EmployeePeriodKey(String email, YearMonth periodAsYearMonth) {
    this.employeeEmail = email;
    this.period = periodAsYearMonth.atEndOfMonth();
  }

  public String getEmployeeEmail() {
    return employeeEmail;
  }

  public LocalDate getPeriod() {
    return period;
  }
}
