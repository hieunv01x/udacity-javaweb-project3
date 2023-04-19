package com.udacity.jdnd.course3.critter.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long employeeSkillId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EmployeeSkillType skill;

    public EmployeeSkill(EmployeeSkillType skill) {
        this.skill = skill;
    }
}
