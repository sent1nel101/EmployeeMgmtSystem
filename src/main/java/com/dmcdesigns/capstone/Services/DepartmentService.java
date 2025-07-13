package com.dmcdesigns.capstone.Services;

import com.dmcdesigns.capstone.Entities.Department;
import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Repositories.DepartmentRepository;
import com.dmcdesigns.capstone.Repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Department> getAllDepartmentsSortedByName() {
        return departmentRepository.findAllDepartmentsSortedByName();
    }

    public Optional<Department> getDepartmentById(Integer id) {
        return departmentRepository.findById(id);
    }

    public Department getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    public Department createDepartment(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new RuntimeException("Department with name '" + department.getName() + "' already exists");
        }
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Integer id, Department departmentDetails) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if name is being changed to an existing name
        if (!department.getName().equals(departmentDetails.getName()) && 
            departmentRepository.existsByName(departmentDetails.getName())) {
            throw new RuntimeException("Department with name '" + departmentDetails.getName() + "' already exists");
        }

        String oldName = department.getName();
        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());

        Department updatedDepartment = departmentRepository.save(department);

        // Update all employees with the new department name if it changed
        if (!oldName.equals(departmentDetails.getName())) {
            updateEmployeeDepartmentNames(oldName, departmentDetails.getName());
        }

        return updatedDepartment;
    }

    @Transactional
    public void deleteDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if any employees are assigned to this department
        List<Employee> employeesInDepartment = getEmployeesByDepartmentName(department.getName());
        if (!employeesInDepartment.isEmpty()) {
            throw new RuntimeException("Cannot delete department '" + department.getName() + 
                    "' because " + employeesInDepartment.size() + " employee(s) are assigned to it");
        }

        departmentRepository.delete(department);
    }

    public List<Department> searchDepartments(String searchTerm) {
        return departmentRepository.searchDepartmentsByText(searchTerm);
    }

    public List<Department> findDepartmentsByNamePattern(String namePattern) {
        return departmentRepository.findDepartmentsByNamePattern(namePattern);
    }

    public Long getTotalDepartmentCount() {
        return departmentRepository.getTotalDepartmentCount();
    }

    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    public List<Employee> getEmployeesByDepartmentName(String departmentName) {
        return employeeRepository.getUsersByDepartment(departmentName).stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeesByDepartmentId(Integer departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));
        return getEmployeesByDepartmentName(department.getName());
    }

    @Transactional
    private void updateEmployeeDepartmentNames(String oldName, String newName) {
        List<Employee> employees = getEmployeesByDepartmentName(oldName);
        for (Employee employee : employees) {
            employee.setDepartment(newName);
            employeeRepository.save(employee);
        }
    }

    public Department transferEmployeesToDepartment(Integer fromDepartmentId, Integer toDepartmentId) {
        Department fromDepartment = departmentRepository.findById(fromDepartmentId)
                .orElseThrow(() -> new RuntimeException("Source department not found with id: " + fromDepartmentId));
        
        Department toDepartment = departmentRepository.findById(toDepartmentId)
                .orElseThrow(() -> new RuntimeException("Target department not found with id: " + toDepartmentId));

        List<Employee> employees = getEmployeesByDepartmentName(fromDepartment.getName());
        for (Employee employee : employees) {
            employee.setDepartment(toDepartment.getName());
            employeeRepository.save(employee);
        }

        return toDepartment;
    }
}
