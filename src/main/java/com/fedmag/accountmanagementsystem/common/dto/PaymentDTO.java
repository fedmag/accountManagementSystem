package com.fedmag.accountmanagementsystem.common.dto;

import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.Salary;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
  private String name;
  private String lastname;
  private String period;
  private String salary;

  public static PaymentDTO from(AppUser user, Salary salary) {
    PaymentDTO paymentDTO = new PaymentDTO();
    paymentDTO.name = user.getName();
    paymentDTO.lastname = user.getLastname();
    paymentDTO.period = formatPeriod(salary.getEmployeePeriodKey().getPeriod());
    paymentDTO.salary = convertSalaryToString(salary.getSalary());
    return paymentDTO;
  }

  private static String formatPeriod(LocalDate period) {
    return period.format(DateTimeFormatter.ofPattern("MMMM-yyyy"));
  }

  private static String convertSalaryToString(Long salary) {
    Long dollars = salary / 100;
    long cents = salary % 100;
    return dollars + " dollar(s) " + cents + " cent(s)";
  }
}
