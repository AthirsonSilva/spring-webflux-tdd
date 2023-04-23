package com.webfluxtdd.controller;

import com.github.javafaker.Faker;
import com.webfluxtdd.dto.EmployeeDTO;
import com.webfluxtdd.entity.Employee;
import com.webfluxtdd.service.EmployeeService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.webfluxtdd.utils.EmployeeMapper.mapToEmployeeDTO;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = EmployeeController.class)
@Log4j2(topic = "EmployeeControllerTest")
class EmployeeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmployeeService employeeService;

    private static final String BASE_URL = "/api/v1/employee";

    private Employee defaultEmployee;

    @BeforeEach
    void setUp() {
        defaultEmployee = Employee.builder()
                .id(UUID.randomUUID().toString())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .build();
    }

    @Test
    @DisplayName("Given a valid employee, when save, then return status code 201")
    public void save() {
        // given - precondition
        EmployeeDTO request = createEmployeeDTO();
        given(employeeService.save(ArgumentMatchers.any(EmployeeDTO.class)))
                .willReturn(Mono.just(request));

        // when - action
        ResponseSpec response = webTestClient.post()
                .uri(BASE_URL)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .exchange();

        // then - assertion
        response
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(responseEntity -> log.info("Response: {}", responseEntity))
                .jsonPath("$.firstName").isEqualTo(request.getFirstName())
                .jsonPath("$.lastName").isEqualTo(request.getLastName())
                .jsonPath("$.email").isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("Given a valid employee id, when findById, then return status code 200")
    public void findById() {
        // given - precondition
        given(employeeService.findById(ArgumentMatchers.anyString()))
                .willReturn(Mono.just(mapToEmployeeDTO(defaultEmployee)));

        // when - action
        ResponseSpec response = webTestClient.get()
                .uri(BASE_URL + "/{id}", defaultEmployee.getId())
                .accept(APPLICATION_JSON)
                .exchange();

        // then - assertion
        response
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(responseEntity -> log.info("Response: {}", responseEntity))
                .jsonPath("$.firstName").isEqualTo(defaultEmployee.getFirstName())
                .jsonPath("$.lastName").isEqualTo(defaultEmployee.getLastName())
                .jsonPath("$.email").isEqualTo(defaultEmployee.getEmail());
    }

    @Test
    @DisplayName("Given multiple employees, when findAll, then return status code 200")
    public void findAll() {
        // given - precondition
        given(employeeService.findAll())
                .willReturn(Flux.just(
                        createEmployeeDTO(),
                        createEmployeeDTO()
                ));

        // when - action
        ResponseSpec response = webTestClient.get()
                .uri(BASE_URL)
                .accept(APPLICATION_JSON)
                .exchange();

        // then - assertion
        response
                .expectStatus().isOk()
                .expectBodyList(EmployeeDTO.class)
                .hasSize(2)
                .consumeWith(responseEntity -> log.info("Response: {}", responseEntity));
    }

    @Test
    @DisplayName("Given a valid employee id, when delete, then return status code 204")
    public void delete() {
        // given - precondition
        given(employeeService.delete(ArgumentMatchers.anyString()))
                .willReturn(Mono.empty());

        // when - action
        ResponseSpec response = webTestClient.delete()
                .uri(BASE_URL + "/{id}", defaultEmployee.getId())
                .accept(APPLICATION_JSON)
                .exchange();

        // then - assertion
        response
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(responseEntity -> log.info("Response: {}", responseEntity));
    }

    @Test
    @DisplayName("Given a valid employee and a valid employee id, when update, then return status code 200")
    public void update() {
        // given - precondition
        EmployeeDTO request = createEmployeeDTO();
        given(employeeService.update(ArgumentMatchers.any(EmployeeDTO.class), ArgumentMatchers.anyString()))
                .willReturn(Mono.just(request));

        // when - action
        ResponseSpec response = webTestClient.put()
                .uri(BASE_URL + "/{id}", defaultEmployee.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .exchange();

        // then - assertion
        response
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(responseEntity -> log.info("Response: {}", responseEntity))
                .jsonPath("$.firstName").isEqualTo(request.getFirstName())
                .jsonPath("$.lastName").isEqualTo(request.getLastName())
                .jsonPath("$.email").isEqualTo(request.getEmail());
    }

    private Employee createEmployee() {
        Faker faker = new Faker();

        return Employee.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .build();
    }

    private EmployeeDTO createEmployeeDTO() {
        Faker faker = new Faker();

        return EmployeeDTO.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .build();
    }
}