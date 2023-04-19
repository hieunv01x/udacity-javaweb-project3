package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.user.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeSkillRepository employeeSkillRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, PetRepository petRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository, EmployeeSkillRepository employeeSkillRepository) {
        this.scheduleRepository = scheduleRepository;
        this.petRepository = petRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.employeeSkillRepository = employeeSkillRepository;
    }

    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        if (this.convertToSchedule(schedule, scheduleDTO)) {
            scheduleRepository.save(schedule);
            return null;
        }
        return this.convertToScheduleDTO(schedule);
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> scheduleList = (List<Schedule>) scheduleRepository.findAll();
        if (scheduleList.isEmpty()) return new ArrayList<>();
        return scheduleList.stream().map(this::convertToScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForPet(long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find the pet"));
        List<Schedule> scheduleList = scheduleRepository.findScheduleByPets(pet);
        if (scheduleList.isEmpty()) return new ArrayList<>();
        return scheduleList.stream().map(this::convertToScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find the employee"));
        List<Schedule> scheduleList = scheduleRepository.findScheduleByEmployees(employee);
        if (scheduleList.isEmpty()) return new ArrayList<>();
        return scheduleList.stream().map(this::convertToScheduleDTO).collect(Collectors.toList());
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find the employee"));
        List<Schedule> scheduleList = scheduleRepository.findScheduleByCustomerId(customerId);
        if (scheduleList.isEmpty()) return new ArrayList<>();
        return scheduleList.stream().map(this::convertToScheduleDTO).collect(Collectors.toList());
    }

    private boolean convertToSchedule(Schedule schedule, ScheduleDTO scheduleDTO) {
        List<Employee> employees = (List<Employee>) employeeRepository.findAllById(scheduleDTO.getEmployeeIds());
        schedule.setEmployees(employees);
        List<Pet> petList = (List<Pet>) petRepository.findAllById(scheduleDTO.getPetIds());
        if (petList.isEmpty()) return false;
        schedule.setPets(petList);
        schedule.setDate(scheduleDTO.getDate());
        Set<EmployeeSkill> employeeSkillList = new HashSet<>();
        for (EmployeeSkillType skill : scheduleDTO.getActivities()) {
            EmployeeSkill employeeSkill = employeeSkillRepository.findEmployeeBySkill(skill.name())
                    .orElseThrow(() -> new EntityNotFoundException("Can't find the Activity: " + skill.name()));
            employeeSkillList.add(employeeSkill);
        }
        if (employeeSkillList.isEmpty()) return false;
        schedule.setActivities(employeeSkillList);
        return true;
    }

    private ScheduleDTO convertToScheduleDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(schedule.getScheduleId());
        List<Employee> employeeList = schedule.getEmployees();
        scheduleDTO.setEmployeeIds(employeeList.stream().map(Employee::getEmployeeId).collect(Collectors.toList()));
        List<Pet> petList = schedule.getPets();
        scheduleDTO.setPetIds(petList.stream().map(Pet::getPetId).collect(Collectors.toList()));
        scheduleDTO.setDate(schedule.getDate());
        Set<EmployeeSkill> activitiesList = schedule.getActivities();
        scheduleDTO.setActivities(activitiesList.stream().map(EmployeeSkill::getSkill).collect(Collectors.toSet()));
        return scheduleDTO;
    }
}
