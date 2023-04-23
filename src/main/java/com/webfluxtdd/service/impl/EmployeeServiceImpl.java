package com.webfluxtdd.service.impl;

import com.webfluxtdd.dto.EmployeeDTO;
import com.webfluxtdd.entity.Employee;
import com.webfluxtdd.repository.EmployeeRepository;
import com.webfluxtdd.service.EmployeeService;
import com.webfluxtdd.utils.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.webfluxtdd.utils.EmployeeMapper.mapToEmployee;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Mono<EmployeeDTO> save(EmployeeDTO employeeDTO) {
        Employee employee = mapToEmployee(employeeDTO);

        return employeeRepository.save(employee)
                .map(EmployeeMapper::mapToEmployeeDTO);
    }

    @Override
    public Flux<EmployeeDTO> findAll() {
        Flux<Employee> employeeFlux = employeeRepository.findAll();

        return employeeFlux
                .map(EmployeeMapper::mapToEmployeeDTO)
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<EmployeeDTO> findById(String id) {
        Mono<Employee> employee = employeeRepository.findById(id).switchIfEmpty(
                Mono.error(new RuntimeException("Employee not found with id: " + id))
        );

        return employee.map(EmployeeMapper::mapToEmployeeDTO);
    }

    @Override
    public Mono<EmployeeDTO> update(EmployeeDTO request, String id) {
        Mono<Employee> employeeMono = employeeRepository.findById(id).switchIfEmpty(
                Mono.error(new RuntimeException("Employee not found with id: " + id)));

        Mono<Employee> updatedEmployee = employeeMono.flatMap((existingEmployee) -> {
            existingEmployee.setFirstName(request.getFirstName());
            existingEmployee.setLastName(request.getLastName());
            existingEmployee.setEmail(request.getEmail());

            return employeeRepository.save(existingEmployee);
        });

        return updatedEmployee
                .map(EmployeeMapper::mapToEmployeeDTO);
    }

    @Override
    public Mono<Void> delete(String id) {
        return employeeRepository.deleteById(id);
    }
}
