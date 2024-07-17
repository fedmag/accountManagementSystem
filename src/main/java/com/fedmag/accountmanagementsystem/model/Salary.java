package com.fedmag.accountmanagementsystem.model;


import com.fedmag.accountmanagementsystem.common.requests.PaymentRequest;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
