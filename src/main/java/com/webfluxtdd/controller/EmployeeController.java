package com.webfluxtdd.controller;

import com.webfluxtdd.dto.EmployeeDTO;
import com.webfluxtdd.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EmployeeDTO> save(@RequestBody EmployeeDTO request) {
        return employeeService.save(request);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<EmployeeDTO> findById(@PathVariable String id) {
        return employeeService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<EmployeeDTO> findAll() {
        return employeeService.findAll();
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<EmployeeDTO> update(@RequestBody EmployeeDTO request, @PathVariable String id) {
        return employeeService.update(request, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return employeeService.delete(id);
    }
}
