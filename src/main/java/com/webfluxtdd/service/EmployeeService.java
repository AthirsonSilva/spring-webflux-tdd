package com.webfluxtdd.service;

import com.webfluxtdd.dto.EmployeeDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    Mono<EmployeeDTO> save(EmployeeDTO employeeDTO);

    Flux<EmployeeDTO> findAll();

    Mono<EmployeeDTO> findById(String id);

    Mono<EmployeeDTO> update(EmployeeDTO employeeDTO, String id);

    Mono<Void> delete(String id);
}
