package com.fedmag.accountmanagementsystem.service;


import com.fedmag.accountmanagementsystem.common.exceptions.BadRequestException;
import com.fedmag.accountmanagementsystem.common.requests.PaymentRequest;
import com.fedmag.accountmanagementsystem.model.EmployeePeriodKey;
import com.fedmag.accountmanagementsystem.model.Salary;
import com.fedmag.accountmanagementsystem.model.SalaryRepo;
import com.fedmag.accountmanagementsystem.model.UserRepo;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final static Logger LOG = LoggerFactory.getLogger(AccountService.class);

  private final UserRepo userRepo;
  private final SalaryRepo salaryRepo;

  public AccountService(UserRepo userRepo, SalaryRepo salaryRepo) {
    this.userRepo = userRepo;
    this.salaryRepo = salaryRepo;
  }


  @Transactional
  public void insertSalaries(List<PaymentRequest> paymentRequests) {
    LOG.info("Saving {} paymentRequests...", paymentRequests.size());
    List<Salary> salaryList = paymentRequests.stream()
        .filter(this::salaryIsValid)
        .filter(this::salaryPeriodPairIsValid)
        .map(Salary::from)
        .toList();
    salaryRepo.saveAll(salaryList);
    LOG.info("Salaries saved.");
  }

  public void updateSalary(PaymentRequest paymentRequest) {
    LOG.info("Updating paymentRequest for {}", paymentRequest.employee());

    assert salaryIsValid(paymentRequest);

    Optional<Salary> salaryOptional = salaryRepo.findByEmployeePeriodKey(
        new EmployeePeriodKey(paymentRequest.employee(), paymentRequest.period()));

    if (salaryOptional.isEmpty()) {
      LOG.info("The pair {}:{} could not be found in the DB.", paymentRequest.employee(), paymentRequest.period());
      salaryRepo.save(Salary.from(paymentRequest));
      return;
    }

    Salary salary = salaryOptional.get();
    salary.setSalary(paymentRequest.salary());
    salaryRepo.save(salary);
    LOG.info("Salary updated.");
  }

  private boolean salaryIsValid(PaymentRequest paymentRequest) {
    if (paymentRequest.salary() < 0) {
      throw new BadRequestException("Salary cant be negative");
    }
    if (!userRepo.existsByEmail(paymentRequest.employee())) {
      throw new BadRequestException("Employee " + paymentRequest.employee() + " is not in our DB");
    }
    return true;
  }

  private boolean salaryPeriodPairIsValid(PaymentRequest paymentRequest) {
    if (salaryRepo.existsByEmployeePeriodKey(
        new EmployeePeriodKey(paymentRequest.employee(), paymentRequest.period()))) {
      throw new BadRequestException("This employee - period pair exists already");
    }
    return true;
  }
}
