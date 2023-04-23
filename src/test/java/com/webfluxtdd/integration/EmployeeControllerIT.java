package com.webfluxtdd.integration;

import com.github.javafaker.Faker;
import com.webfluxtdd.dto.EmployeeDTO;
import com.webfluxtdd.integration.base.AbstractTestContainer;
import com.webfluxtdd.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class EmployeeControllerIT extends AbstractTestContainer {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URL = "/api/v1/employee";

    @Test
    @DisplayName("Should save employee")
    public void save() {
        // given - precondition
        EmployeeDTO request = createEmployeeDTO();

        // when - action
        ResponseSpec response = webTestClient
                .post()
                .uri(BASE_URL)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange();

        // then - assertion
        response
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.firstName").isEqualTo(request.getFirstName())
                .jsonPath("$.lastName").isEqualTo(request.getLastName())
                .jsonPath("$.email").isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("Should find employee by id")
    public void findById() {
        // given - precondition
        EmployeeDTO request = employeeService.save(createEmployeeDTO()).block();

        // when - action
        assert request != null;
        ResponseSpec response = webTestClient
                .get()
                .uri(BASE_URL + "/{id}", request.getId())
                .exchange();

        // then - assertion
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(request.getId())
                .jsonPath("$.firstName").isEqualTo(request.getFirstName())
                .jsonPath("$.lastName").isEqualTo(request.getLastName())
                .jsonPath("$.email").isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("Should find all employees")
    public void findAll() {
        // given - precondition
        for (int i = 0; i < 2; i++) {
            employeeService.save(createEmployeeDTO()).block();
        }

        // when - action
        ResponseSpec response = webTestClient
                .get()
                .uri(BASE_URL)
                .exchange();

        // then - assertion
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isNotEmpty()
                .jsonPath("$[0].id").isNotEmpty()
                .jsonPath("$[1].id").isNotEmpty();
    }

    @Test
    @DisplayName("Should update employee")
    public void delete() {
        // given - precondition
        EmployeeDTO request = employeeService.save(createEmployeeDTO()).block();

        // when - action
        assert request != null;
        ResponseSpec response = webTestClient
                .delete()
                .uri(BASE_URL + "/{id}", request.getId())
                .exchange();

        // then - assertion
        response
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Should update employee")
    public void update() {
        // given - precondition
        EmployeeDTO request = employeeService.save(createEmployeeDTO()).block();

        // when - action
        assert request != null;
        request.setFirstName("Jane");
        request.setEmail("jane.doe@gmail.com");

        ResponseSpec response = webTestClient
                .put()
                .uri(BASE_URL + "/{id}", request.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange();

        // then - assertion
        response
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(request.getId())
                .jsonPath("$.firstName").isEqualTo(request.getFirstName())
                .jsonPath("$.lastName").isEqualTo(request.getLastName())
                .jsonPath("$.email").isEqualTo(request.getEmail());
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
