package com.example.departmentcrud.repository;

import com.example.departmentcrud.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Basic CRUD methods (save, findById, findAll, deleteById, existsById) are
    // inherited from JpaRepository - no extra code needed for the required operations.
}
