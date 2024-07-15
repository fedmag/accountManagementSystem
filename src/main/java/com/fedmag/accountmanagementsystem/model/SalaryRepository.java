package com.fedmag.accountmanagementsystem.model;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SalaryRepository extends CrudRepository<Salary, String> {

  boolean existsByEmployeePeriodKey(EmployeePeriodKey employeePeriodKey);

  Optional<Salary> findByEmployeePeriodKey(EmployeePeriodKey employeePeriodKey);

  @Query("SELECT s FROM Salary s WHERE s.employeePeriodKey.employeeEmail = ?1 ORDER BY s.employeePeriodKey.period DESC")
  List<Salary> findByEmailOrderByPeriodDesc(String employeeEmail);

}
