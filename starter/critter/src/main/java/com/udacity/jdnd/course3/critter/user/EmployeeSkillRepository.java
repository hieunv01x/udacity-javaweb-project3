package com.udacity.jdnd.course3.critter.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeSkillRepository extends CrudRepository<EmployeeSkill, Long> {
    @Query(nativeQuery = true,
            value = "select es.* from employee_skill es where es.skill = ?1")
    Optional<EmployeeSkill> findEmployeeBySkill(String skill);
}
