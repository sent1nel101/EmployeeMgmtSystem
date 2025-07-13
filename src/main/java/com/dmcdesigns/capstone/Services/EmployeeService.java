package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    public Optional<Employee> getEmployeeById(Integer id) {
        Optional<User> user = employeeRepository.findById(id);
        return user.map(u -> u instanceof Employee ? (Employee) u : null);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setPassword(employeeDetails.getPassword());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setRole(employeeDetails.getRole());

        if (employeeDetails.hasAccess()) {
            employee.grantAccess();
        } else {
            employee.revokeAccess();
        }

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Integer id) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.getUsersByDepartment(department).stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    public Employee grantEmployeeAccess(Integer id) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employee.grantAccess();
        return employeeRepository.save(employee);
    }

    public Employee revokeEmployeeAccess(Integer id) {
        Employee employee = (Employee) employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employee.revokeAccess();
        return employeeRepository.save(employee);
    }
}
