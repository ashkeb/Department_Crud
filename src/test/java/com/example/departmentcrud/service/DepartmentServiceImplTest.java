package com.example.departmentcrud.service;

import com.example.departmentcrud.dto.DepartmentDTO;
import com.example.departmentcrud.entity.Department;
import com.example.departmentcrud.exception.ResourceNotFoundException;
import com.example.departmentcrud.repository.DepartmentRepository;
import com.example.departmentcrud.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentServiceImpl unit tests")
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentDTO departmentDTO;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Human Resources")
                .build();

        departmentDTO = DepartmentDTO.builder()
                .id(1L)
                .name("Human Resources")
                .build();
    }

    @Test
    @DisplayName("createDepartment() should save and return the created department")
    void createDepartment_shouldReturnSavedDepartment() {
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentDTO result = departmentService.createDepartment(departmentDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Human Resources");
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("getDepartmentById() should return department when found")
    void getDepartmentById_whenFound_shouldReturnDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        DepartmentDTO result = departmentService.getDepartmentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getDepartmentById() should throw ResourceNotFoundException when not found")
    void getDepartmentById_whenNotFound_shouldThrowException() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(departmentRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("getAllDepartments() should return list of all departments")
    void getAllDepartments_shouldReturnAllDepartments() {
        Department department2 = Department.builder().id(2L).name("Finance").build();
        when(departmentRepository.findAll()).thenReturn(Arrays.asList(department, department2));

        List<DepartmentDTO> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Human Resources");
        assertThat(result.get(1).getName()).isEqualTo("Finance");
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("updateDepartment() should update and return updated department when found")
    void updateDepartment_whenFound_shouldReturnUpdatedDepartment() {
        DepartmentDTO updateRequest = DepartmentDTO.builder()
                .name("Human Resources & Talent")
                .build();

        Department updatedEntity = Department.builder()
                .id(1L)
                .name("Human Resources & Talent")
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedEntity);

        DepartmentDTO result = departmentService.updateDepartment(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Human Resources & Talent");
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("updateDepartment() should throw ResourceNotFoundException when department not found")
    void updateDepartment_whenNotFound_shouldThrowException() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.updateDepartment(99L, departmentDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    @DisplayName("deleteDepartment() should delete when department exists")
    void deleteDepartment_whenExists_shouldDelete() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(1L);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).existsById(1L);
        verify(departmentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteDepartment() should throw ResourceNotFoundException when department does not exist")
    void deleteDepartment_whenNotExists_shouldThrowException() {
        when(departmentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> departmentService.deleteDepartment(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(departmentRepository, never()).deleteById(anyLong());
    }
}
