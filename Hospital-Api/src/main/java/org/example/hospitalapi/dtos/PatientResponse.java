package org.example.hospitalapi.dtos;

import lombok.*;
import org.example.hospitalapi.model.Employee;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PatientResponse {
  private Long id;
  private String name;
  private LocalDate dateOfBirth;
  private Employee admittedBy;
}
