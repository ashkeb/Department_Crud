package com.example.departmentcrud.controller;

import com.example.departmentcrud.dto.DepartmentDTO;
import com.example.departmentcrud.exception.ResourceNotFoundException;
import com.example.departmentcrud.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
@DisplayName("DepartmentController web layer tests")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    @Test
    @DisplayName("POST /api/v1/departments should create a department and return 201")
    void createDepartment_shouldReturn201() throws Exception {
        DepartmentDTO request = DepartmentDTO.builder().name("Human Resources").build();
        DepartmentDTO response = DepartmentDTO.builder().id(1L).name("Human Resources").build();

        when(departmentService.createDepartment(any(DepartmentDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Human Resources"));

        verify(departmentService, times(1)).createDepartment(any(DepartmentDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/departments with blank name should return 400")
    void createDepartment_withInvalidPayload_shouldReturn400() throws Exception {
        DepartmentDTO invalidRequest = DepartmentDTO.builder().name("").build();

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(departmentService, never()).createDepartment(any(DepartmentDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/departments/{id} should return department when found")
    void getDepartmentById_whenFound_shouldReturn200() throws Exception {
        DepartmentDTO response = DepartmentDTO.builder().id(1L).name("Human Resources").build();
        when(departmentService.getDepartmentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/departments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Human Resources"));
    }

    @Test
    @DisplayName("GET /api/v1/departments/{id} should return 404 when not found")
    void getDepartmentById_whenNotFound_shouldReturn404() throws Exception {
        when(departmentService.getDepartmentById(99L))
                .thenThrow(new ResourceNotFoundException("Department not found with id: 99"));

        mockMvc.perform(get("/api/v1/departments/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Department not found with id: 99"));
    }

    @Test
    @DisplayName("GET /api/v1/departments should return list of departments")
    void getAllDepartments_shouldReturn200() throws Exception {
        List<DepartmentDTO> departments = Arrays.asList(
                DepartmentDTO.builder().id(1L).name("Human Resources").build(),
                DepartmentDTO.builder().id(2L).name("Finance").build()
        );
        when(departmentService.getAllDepartments()).thenReturn(departments);

        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Human Resources"))
                .andExpect(jsonPath("$[1].name").value("Finance"));
    }

    @Test
    @DisplayName("PUT /api/v1/departments/{id} should update and return 200")
    void updateDepartment_whenFound_shouldReturn200() throws Exception {
        DepartmentDTO request = DepartmentDTO.builder().name("Human Resources & Talent").build();
        DepartmentDTO response = DepartmentDTO.builder().id(1L).name("Human Resources & Talent").build();

        when(departmentService.updateDepartment(eq(1L), any(DepartmentDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/departments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Human Resources & Talent"));
    }

    @Test
    @DisplayName("PUT /api/v1/departments/{id} should return 404 when department not found")
    void updateDepartment_whenNotFound_shouldReturn404() throws Exception {
        DepartmentDTO request = DepartmentDTO.builder().name("Human Resources & Talent").build();

        when(departmentService.updateDepartment(eq(99L), any(DepartmentDTO.class)))
                .thenThrow(new ResourceNotFoundException("Department not found with id: 99"));

        mockMvc.perform(put("/api/v1/departments/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/departments/{id} should return 204 when deleted")
    void deleteDepartment_whenFound_shouldReturn204() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/v1/departments/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/departments/{id} should return 404 when not found")
    void deleteDepartment_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Department not found with id: 99"))
                .when(departmentService).deleteDepartment(99L);

        mockMvc.perform(delete("/api/v1/departments/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // static import helper for eq(); kept local to avoid pulling in unrelated matchers
    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
