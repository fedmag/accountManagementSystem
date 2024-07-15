package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.dto.PaymentDTO;
import com.fedmag.accountmanagementsystem.model.AppUser;
import com.fedmag.accountmanagementsystem.model.EmployeePeriodKey;
import com.fedmag.accountmanagementsystem.model.Salary;
import com.fedmag.accountmanagementsystem.model.SalaryRepository;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

  private final static Logger LOG = LoggerFactory.getLogger(EmployeeService.class);

  private final UserRepo userRepo;
  private final SalaryRepository salaryRepository;

  public EmployeeService(UserRepo userRepo, SalaryRepository salaryRepository) {
    this.userRepo = userRepo;
    this.salaryRepository = salaryRepository;
  }

  public PaymentDTO getPayment(String email, YearMonth period) {
    AppUser user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException(
        "Unable to find %s".formatted(email)));
    Salary salary = salaryRepository.findByEmployeePeriodKey(new EmployeePeriodKey(email,  period))
        .orElseThrow(() -> new RuntimeException(
            "Unable to find %s for period: %s".formatted(email, period)));
    return PaymentDTO.from(user, salary);
  }

  public List<PaymentDTO> getAllPayments(String email) {
    List<PaymentDTO> allPayments;
    AppUser user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException(
        "Unable to find %s".formatted(email)));
    List<Salary> salaryList = salaryRepository.findByEmailOrderByPeriodDesc(
        email);
    allPayments = new ArrayList<>(
        salaryList.stream().map(s -> PaymentDTO.from(user, s)).toList());
    return allPayments;
  }
}
