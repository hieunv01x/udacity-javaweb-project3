package com.udacity.jdnd.course3.critter.user;

import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeSkillRepository employeeSkillRepository;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeSkillRepository employeeSkillRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeSkillRepository = employeeSkillRepository;
    }

    public EmployeeDTO getEmployee(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Can't find the employeeId: " + employeeId));
        return this.convertToEmployeeDTO(employee);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<Employee> employees = employeeRepository.findAllByDaysAvailableContaining(employeeDTO.getDate().getDayOfWeek());
        Set<EmployeeSkill> employeeSkillSet = new HashSet<>();
        for (EmployeeSkillType skill : employeeDTO.getSkills()) {
            EmployeeSkill employeeSkill = employeeSkillRepository.findEmployeeBySkill(skill.name())
                    .orElseThrow(() -> new EntityNotFoundException("Can't find the skill"));
            employeeSkillSet.add(employeeSkill);
        }
        List<Employee> availableEmployees = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getSkills().containsAll(employeeSkillSet)) {
                availableEmployees.add(employee);
            }
        }
        return availableEmployees.stream().map(this::convertToEmployeeDTO).collect(Collectors.toList());
    }

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {

        Employee employee = new Employee();
        String message = this.convertToEmployee(employee, employeeDTO);
        if (message == null) {
            employeeRepository.save(employee);
            return this.convertToEmployeeDTO(employee);
        }
        return null;
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("Can't find the employeeId: " + employeeId));
        employee.setDaysAvailable(daysAvailable);
        employeeRepository.save(employee);
    }

    private String convertToEmployee(Employee employee, EmployeeDTO employeeDTO) {
        employee.setName(employeeDTO.getName());
        Set<EmployeeSkill> employeeSkillList = new HashSet<>();
        for (EmployeeSkillType skill : employeeDTO.getSkills()) {
            EmployeeSkill employeeSkill = employeeSkillRepository.findEmployeeBySkill(skill.name())
                    .orElseThrow(() -> new EntityNotFoundException("Can't find the Activity: " + skill.name()));
            employeeSkillList.add(employeeSkill);
        }
        if (employeeSkillList.isEmpty()) {
            return "Can't find the Activity";
        }
        employee.setSkills(employeeSkillList);
        if (employeeDTO.getDaysAvailable() == null) return null;
        employee.setDaysAvailable(employeeDTO.getDaysAvailable());
        return null;
    }

    private EmployeeDTO convertToEmployeeDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getEmployeeId());
        employeeDTO.setName(employee.getName());
        Set<EmployeeSkill> employeeSkillSet = employee.getSkills();
        employeeDTO.setSkills(employeeSkillSet.stream().map(EmployeeSkill::getSkill).collect(Collectors.toSet()));
        employeeDTO.setDaysAvailable(employee.getDaysAvailable());
        return employeeDTO;
    }
}
