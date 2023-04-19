package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.user.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    @Query(nativeQuery = true,
            value = "select s.* from Schedule s, Pet p where s.pet_id = p.pet_id and p.pet_id = ?1")
    List<Schedule> findScheduleByPetId(Long petId);

    List<Schedule> findScheduleByPets(Pet pet);

    @Query(nativeQuery = true,
            value = "select s.* from Schedule s, Employee e where s.employee_id = e.employee_id and e.employee_id = ?1")
    List<Schedule> findScheduleByEmployeeId(Long employeeId);

    List<Schedule> findScheduleByEmployees(Employee employee);

    @Query(nativeQuery = true,
            value = "select s.* from Schedule s, Customer c where s.customer_id = c.customer_id and c.customer_id = ?1")
    List<Schedule> findScheduleByCustomerId(Long customerId);
}
