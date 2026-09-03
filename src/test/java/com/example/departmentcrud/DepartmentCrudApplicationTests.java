package com.example.departmentcrud;

import com.example.departmentcrud.dto.DepartmentDTO;
import com.example.departmentcrud.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end style test that boots the full Spring context (with an H2
 * in-memory DB via the "test" profile) and exercises the real CRUD flow
 * through the HTTP layer.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DepartmentCrudApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    @DisplayName("Spring application context loads successfully")
    void contextLoads() {
    }

    @AfterEach
    void cleanUp() {
        departmentRepository.deleteAll();
    }

    @Test
    @DisplayName("Full CRUD lifecycle works end-to-end through the REST API")
    void fullCrudLifecycle_shouldWorkEndToEnd() throws Exception {
        DepartmentDTO createRequest = DepartmentDTO.builder()
                .name("Human Resources")
                .build();

        // CREATE
        String responseBody = mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Human Resources"))
                .andReturn().getResponse().getContentAsString();

        DepartmentDTO created = objectMapper.readValue(responseBody, DepartmentDTO.class);
        Long id = created.getId();

        // RETRIEVE (by id)
        mockMvc.perform(get("/api/v1/departments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Human Resources"));

        // RETRIEVE (all)
        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // UPDATE
        DepartmentDTO updateRequest = DepartmentDTO.builder()
                .name("Human Resources & Talent")
                .build();

        mockMvc.perform(put("/api/v1/departments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Human Resources & Talent"));

        // DELETE
        mockMvc.perform(delete("/api/v1/departments/{id}", id))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/v1/departments/{id}", id))
                .andExpect(status().isNotFound());
    }
}
