package org.example.hospitalapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Table(name = "patient_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamicUpdate
public class Patient {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "patient_id")
  private Long id;
  @Column(name = "patient_name")
  private String name;
  @Column(name = "patient_date_of_birth")
  private LocalDate dateOfBirth;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "admitted_by")
  @JsonBackReference
  private Employee admittedBy;

}
