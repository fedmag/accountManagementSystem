package com.fedmag.accountmanagementsystem.model;


import com.fedmag.accountmanagementsystem.common.requests.PaymentRequest;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Salaries")
public class Salary {

  @EmbeddedId
  private EmployeePeriodKey employeePeriodKey;
  private Long salary;

  public Salary() {
  }

  public static Salary from(PaymentRequest request) {
    Salary salary = new Salary();
    salary.setEmployeePeriodKey(new EmployeePeriodKey(request.employee(), request.period()));
    salary.setSalary(request.salary());
    return salary;
  }

  public EmployeePeriodKey getEmployeePeriodKey() {
    return employeePeriodKey;
  }

  public void setEmployeePeriodKey(EmployeePeriodKey employeePeriodKey) {
    this.employeePeriodKey = employeePeriodKey;
  }

  public Long getSalary() {
    return salary;
  }

  public void setSalary(Long salary) {
    this.salary = salary;
  }
}
