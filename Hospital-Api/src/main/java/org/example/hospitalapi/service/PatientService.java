package org.example.hospitalapi.service;

import org.example.hospitalapi.dtos.EmployeeResponse;
import org.example.hospitalapi.dtos.PatientResponse;
import org.example.hospitalapi.dtos.PostPatientRequest;
import org.example.hospitalapi.dtos.UpdatePatientRequest;
import org.example.hospitalapi.enums.EmployeeStatus;
import org.example.hospitalapi.mapper.EmployeeMapper;
import org.example.hospitalapi.mapper.PatientMapper;
import org.example.hospitalapi.model.Employee;
import org.example.hospitalapi.model.Patient;
import org.example.hospitalapi.repository.EmployeeRepository;
import org.example.hospitalapi.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class PatientService {
  @Autowired
  private PatientRepository patientRepository;
  private final PatientMapper patientMapper = new PatientMapper();
  @Autowired
  private EmployeeService employeeService;
  @Autowired
  private EmployeeMapper employeeMapper;
  @Autowired
  private EmployeeRepository employeeRepository;

  /*
  Get methods
   */

  public List<PatientResponse> getAllPatients() {
    List<Patient> patients = patientRepository.findAll();
    return patientMapper.toDtoGetPatientsResponseList(patients);
  }

  public Optional<PatientResponse> getPatientById(Long id) {
    Optional<Patient> patient = patientRepository.findById(id);
    return patient.map(patientMapper::toDtoGetPatientResponse);
  }

  public List<PatientResponse> getPatientsByBirthDateRange(LocalDate from, LocalDate to) {
    List<Patient> patients = patientRepository.findPatientsByDateOfBirthBetween(from, to);
    return patientMapper.toDtoGetPatientsResponseList(patients);
  }

  public List<PatientResponse> getPatientsByMedicalDepartment(String medicalDepartment) {
    List<Patient> patients = patientRepository.findPatientsByAdmittedBy_Department(medicalDepartment);
    return patientMapper.toDtoGetPatientsResponseList(patients);
  }

  public List<PatientResponse> getPatientsByMedicalStatus(Integer employeeStatus) {
    List<Patient> patients = (employeeStatus == 0) ? patientRepository.findPatientsByAdmittedByEmployeeStatus(EmployeeStatus.ON_CALL) : (employeeStatus == 1) ? patientRepository.findPatientsByAdmittedByEmployeeStatus(EmployeeStatus.ON) : (employeeStatus == 2) ? patientRepository.findPatientsByAdmittedByEmployeeStatus(EmployeeStatus.ON) : null;
    return patients != null ? patientMapper.toDtoGetPatientsResponseList(patients) : null;
  }

  /*
  Post methods
   */

  /**
   * Method to create a new patient.
   *
   * @param postPatientRequest Object request that must have name, dateOfBirth and employeeId.
   * @return Returns an Optional<PostPatientResponse> that can contain a PostPatientResponse object or is empty.
   */
  public Optional<PatientResponse> createPatient(PostPatientRequest postPatientRequest) {
    Optional<EmployeeResponse> employeeResponse = employeeService.getEmployeeById(postPatientRequest.getEmployee());
    if (employeeResponse.isPresent()) {
      Patient newPatient = patientMapper.fromPostPatientRequestToPatient(postPatientRequest);
      newPatient.setAdmittedBy(employeeMapper.fromDtoEmployeeResponseToEmployee(employeeResponse.get()));
      patientRepository.save(newPatient);
      return Optional.of(patientMapper.fromPatientToDtoPatientResponse(newPatient));
    } else {
      return Optional.empty();
    }
  }

  /*
  Put method
   */
  public Optional<PatientResponse> updatePatient(Long id, UpdatePatientRequest updatePatientRequest) {
    Optional<Patient> optionalPatient = patientRepository.findById(id);
    if (optionalPatient.isPresent()) {
      return getUpdatedPatient( updatePatientRequest ,optionalPatient.get());
    }else {
      return Optional.empty();
    }
  }

  private Optional<Employee> getOptionalEmployee(Long employeeId){
    return employeeService.getEmployeeById(employeeId).map(employeeMapper::fromDtoEmployeeResponseToEmployee);
  }

  private Optional<PatientResponse> getUpdatedPatient(UpdatePatientRequest updatePatientRequest, Patient patient){
    if (updatePatientRequest.getName().isPresent()){
      //Settear nombre
      patient.setName(updatePatientRequest.getName().get());
    }
    if (updatePatientRequest.getDateOfBirth().isPresent()){
      //Settear dateOfBirth
      patient.setDateOfBirth(updatePatientRequest.getDateOfBirth().get());
    }
    if (updatePatientRequest.getEmployee().isPresent()){
      //Settear employee
      Optional<Employee> optionalEmployee = getOptionalEmployee(updatePatientRequest.getEmployee().get());
      optionalEmployee.ifPresent(patient::setAdmittedBy);
    }
    return Optional.of(patientMapper.fromPatientToDtoPatientResponse(patientRepository.save(patient)));
  }

}